package dk.andersbohn.resume.service

// import dk.andersbohn.resume.service.ResumeHtml.resume

import dk.andersbohn.resume.domain.Resume
import dk.andersbohn.resume.service.gist.{Gist, GistLive}
import zio._
import zio.http._
import zio.json._
import zio._
import zio.http.Header.{AccessControlAllowOrigin, Origin}
import zio.http.Middleware.{cors, CorsConfig}
import zio.http.template._

object Backend extends ZIOAppDefault {

  def staticResourcesPublic =
    Routes(Method.GET / "public" / trailing -> handler {
      val extractPath    = Handler.param[(Path, Request)](_._1)
      val extractRequest = Handler.param[(Path, Request)](_._2)

      for {
        path <- extractPath

        file <- Handler.getResourceAsFile("./public/" + path.encode)
        http <-
        // Rendering a custom UI to list all the files in the directory
        extractRequest >>> (if (file.isDirectory) {
                              // Accessing the files in the directory
                              val files = file.listFiles.toList.sortBy(_.getName)
                              val base  = "./public/"
                              val rest  = path

                              // Custom UI to list all the files in the directory
                              Handler.template(s"File Explorer ~$base${path}") {
                                ul(
                                  li(a(href := s"$base$rest", "..")),
                                  files.map { file =>
                                    li(
                                      a(
                                        href := s"$base${path.encode}${if (path.isRoot) file.getName
                                        else "/" + file.getName}",
                                        file.getName
                                      )
                                    )
                                  }
                                )
                              }
                            }

                            // Return the file if it's a static resource
                            else if (file.isFile) Handler.fromFile(file)

                            // Return a 404 if the file doesn't exist
                            else Handler.notFound("sheet"))
      } yield http
    }).sandbox

  def routes(resumeJson: Resume): Routes[Any, Response] =
    (Handler.html {
      ResumeHtml.resume(resumeJson)
    }.toRoutes ++ staticResourcesPublic) @@ cors(corsConfig)

  val sslConfig =
    SSLConfig.fromResource(behaviour = SSLConfig.HttpBehaviour.Accept, certPath = "server.crt", keyPath = "server.key")

  val corsConfig: CorsConfig =
    CorsConfig(allowedOrigin = {
      case origin if origin == Origin.parse("http://localhost:8090").toOption.get =>
        Some(AccessControlAllowOrigin.Specific(origin))
      case _                                                                      => None
    })

  private val config = Server.Config.default
    .port(8090)
    .ssl(sslConfig)

  private val configLayer = ZLayer.succeed(config)

  override val run = (for {
    resume <- Gist.theJson
    _      <- Server.serve(routes(resume))
  } yield ()).provide(configLayer, Server.live, GistLive.resource)

}
