package dk.bohnjespersen.anders.resume

import dk.andersbohn.resume.service.gist.GistLive
import zio.test.*
import zio.http.*

object MainAppSpec extends ZIOSpecDefault {

  def spec = suite("suite for MainApp")(
    test("test for endpoint /text") {
      val request = Request.get(URL(Path.decode("/")))
      MainApp.routes.runZIO(request) *> assertTrue(true)
    }
  ).provide(GistLive.live)

}
