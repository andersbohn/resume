package dk.bohnjespersen.anders.resume

import dk.andersbohn.resume.service.ResumeHtml
import dk.andersbohn.resume.service.gist.{Gist, GistLive}
import dk.bohnjespersen.anders.resume.domain.types.Lang
import zio.ZIO
import zio.http.*
import zio.test.*
import zio.test.Assertion.equalTo

import scala.io.Source

object GistSpec extends ZIOSpecDefault {
  def spec = suite("suite for Gist resource")(
    test("test resume_en.json english") {
      val source  = Source.fromResource("base/resume_en.json")
      val request = Request.get(URL(Path.decode("/")))
      ZIO.serviceWithZIO[Gist.Service](gistJson => gistJson.resume(Lang.safeOrEnglish("dk"), None),
      ) *> assertTrue(true)
    },
    test("test resume_en.json german") {
      val source  = Source.fromResource("base/resume_en.json")
      val request = Request.get(URL(Path.decode("/")))
      ZIO
        .serviceWithZIO[Gist.Service](gistJson => gistJson.resume(Lang.de, None))
        .map(j => assert(j.basics.label)(equalTo("Scala Entwickler")))
    },
    test("test resume_en.json") {
      val source  = Source.fromResource("messages/messages_en.json")
      val request = Request.get(URL(Path.decode("/")))
      ZIO
        .serviceWithZIO[Gist.Service](gistJson => gistJson.messages(Lang.en))
        .map { j =>
          assert(j.mfk("skills_heading"))(equalTo("Skills")) &&
          assert(j.mfks("skills_list").length)(equalTo(4))
        }
    },
    test("test messages_de.json english") {
      val source  = Source.fromResource("base/resume_en.json")
      val request = Request.get(URL(Path.decode("/")))
      ZIO.serviceWithZIO[Gist.Service](gistJson => gistJson.resume(Lang.safeOrEnglish("dk"), None),
      ) *> assertTrue(true)
    },
  ).provide(GistLive.resource)
}
