package dk.bohnjespersen.anders.resume

import dk.andersbohn.resume.service.ResumeHtml
import dk.andersbohn.resume.service.gist.{Gist, GistLive}
import zio.*
import zio.http.*

import dk.bohnjespersen.anders.resume.domain.types._

object MainApp extends ZIOAppDefault {
  private def serveResource(resourcePath: String, req: Request) =
    Handler
      .fromResource(resourcePath)
      .apply(req)
      .tapError(err => Console.printLine(s"error -> $err"))
      .map { response =>
        if (req.method == Method.HEAD) response.copy(body = Body.empty)
        else response
      }
      .tapError(e => Console.printLine(s"Failed to serve resource $resourcePath: $e"))
      .orDie

  def makeWebAssets: Routes[Any, Nothing] = Routes(
    Method.GET / "assets" / trailing  -> handler { (path: Path, req: Request) =>
      serveResource(s"${path.dropLeadingSlash.encode}", req)
    },
    Method.HEAD / "assets" / trailing -> handler { (path: Path, req: Request) =>
      serveResource(s"${path.dropLeadingSlash.encode}", req)
    },
  )

  def makeWebApp =
    Routes(
      Method.GET / string("lang") / string("aspect") -> handler {
        (strLang: String, strAspect: String, req: Request) =>
          val deko     = req.queryParameters.queryParam("deko").isDefined
          val aspect   = Aspect.safeOrSwe(strAspect)
          val lang     = Lang.safeOrEnglish(strLang)
          val reqPatch = req
            .queryParameters
            .queryParam("patch")
          val patch    = aspectOrLangPatch(reqPatch, aspect, lang)

          ZIO
            .serviceWithZIO[Gist.Service](gistJson =>
              for {
                messages <- gistJson.messages(lang)
                resume   <- gistJson.resume(lang, patch)
                ht = ResumeHtml.resume(lang, aspect)(using resume, messages, deko)
              } yield Response.html(ht),
            )
            .tapError(e => Console.printLine(s"Failed to serve resource $lang $deko $aspect : $e"))
            .orDie
      },
    )

  private[resume] def aspectOrLangPatch(
      reqPatch: Option[String],
      aspect: Aspect,
      lang: Lang,
  ): Option[String] =
    reqPatch.orElse(
      if (aspect == Aspect.sde)
        Some(if (lang == Lang.en) s"resume_sde.json" else s"resume_sde_$lang.json")
      else None,
    )

  val routes = makeWebAssets ++ makeWebApp

  override val run = Server.serve(routes).provide(Server.default, GistLive.resource)
}
