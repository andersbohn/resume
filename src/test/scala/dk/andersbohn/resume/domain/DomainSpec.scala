package dk.andersbohn.resume.domain
import zio.ZIO
import zio.test.Assertion.equalTo
import zio.test._
import zio._
import zio.test._
import zio.test.Assertion._

import zio.json._

import scala.io.Source

object DomainSpec extends ZIOSpecDefault {

  def spec = suite("SomeQuality")(test("from str to json to code ") {
    for {
      jsonSample <- ZIO.succeed(Source.fromResource("resume-sample.json").getLines().mkString("\n"))
      resume     <- ZIO.succeed(jsonSample.fromJson[Resume])
    } yield assert(resume.map(_.basics.name))(equalTo(Right("WIP Anders Bohn Jespersen"))) && assert(
      resume.map(_.awards.headOption.map(_.summary))
    )(equalTo(Right(Some("Biggy"))))
  })

}
