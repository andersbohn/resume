package dk.bohnjespersen.anders.resume.util

import java.nio.file.{Files, Paths}

import dk.andersbohn.resume.service.ResumeHtml
import dk.andersbohn.resume.service.gist.{Gist, GistLive}
import zio.*
import zio.http.{Response, Server}

import dk.bohnjespersen.anders.resume.MainApp
import dk.bohnjespersen.anders.resume.MainApp.aspectOrLangPatch
import dk.bohnjespersen.anders.resume.domain.types.{Aspect, Lang}

object StaticExporter extends ZIOAppDefault {
  val languages = List(Lang.en, Lang.de)
  val aspects   = List(Aspect.swe, Aspect.sde)

  val outputDir = "target/dist"

  private def generateRedirectsFile(outputDir: String): Task[Unit] = ZIO.attempt {
    val content =
      """|# Resume Redirects
         |/    /en    302
         |/en  /en/swe/   301
         |/de  /de/swe/   301
         |/en/swe  /en/swe/   301
         |/de/swe  /de/swe/   301
         |/en/sde  /en/swe/   301
         |/de/sde  /de/swe/   301
      """.stripMargin

    Files.writeString(Paths.get(s"$outputDir/_redirects"), content)
  }
  def run                                                          = (for {
    _ <- Console.printLine("Starting static export...")
    _ <- ZIO.attempt(Files.createDirectories(Paths.get(outputDir)))

    _ <- ZIO.foreachDiscard(aspects) { aspect =>
      ZIO.foreachDiscard(languages) { lang =>
        for {
          response <- ZIO
            .serviceWithZIO[Gist.Service](gistJson =>
              for {
                messages <- gistJson.messages(lang)
                patch  = MainApp.aspectOrLangPatch(None, aspect, lang)
                resume   <- gistJson.resume(lang, patch)
                ht = ResumeHtml.resume(lang, aspect)(using resume, messages, false)
              } yield Response.html(ht),
            )

          _ <- ZIO.attempt(Files.createDirectories(Paths.get(s"$outputDir/$lang/$aspect")))
          htlmContent <- response.body.asString
          _           <- ZIO.attempt(
            Files.writeString(Paths.get(s"$outputDir/$lang/$aspect/index.html"), htlmContent),
          )
          _ <- Console.printLine(s"Exported $lang version to $outputDir/$lang/$aspect/index.html")
        } yield ()
      }
    }
    _ <- generateRedirectsFile(outputDir)
    _ <- Console.printLine(s"Exported _redirects to $outputDir/")
  } yield ()).provide(GistLive.resource)

  def getGitHash: String = sys.env.getOrElse("GITHUB_SHA", "local-dev").take(7)
}
