package dk.andersbohn.resume.domain

import java.time.LocalDate

import dk.andersbohn.resume.domain.Basics.Url
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

object Interest {
  implicit val encoderInterest: JsonEncoder[Interest] = DeriveJsonEncoder.gen[Interest]
  implicit val decoderInterest: JsonDecoder[Interest] = DeriveJsonDecoder.gen[Interest]
}

case class Interest(name: String, keywords: List[String]) extends CvItem {
  override def summary: String        = keywords.headOption.getOrElse("")
  override def features: List[String] = keywords.drop(1)
  override def company: String        = name
  override def title: String          = ""
  override def timeSpan: String       = ""
}
