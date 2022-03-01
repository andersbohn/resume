package dk.andersbohn.resume.domain

import dk.andersbohn.resume.domain.Basics.Url
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

import java.time.LocalDate
object Skill {
  implicit val encoderSkill: JsonEncoder[Skill] = DeriveJsonEncoder.gen[Skill]
  implicit val decoderSkill: JsonDecoder[Skill] = DeriveJsonDecoder.gen[Skill]
}

case class Skill(name: String, keywords: List[String]) extends CvItem {
  override def summary: Url           = ""
  override def features: List[String] = keywords
  override def company: String        = name
  override def title: String          = ""
  override def timeSpan: String       = ""
}
