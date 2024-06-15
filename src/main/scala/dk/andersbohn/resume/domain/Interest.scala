package dk.andersbohn.resume.domain

import dk.andersbohn.resume.domain.Basics.Url
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

import java.time.LocalDate

object Interest {
  implicit val encoderInterest: JsonEncoder[Interest] = DeriveJsonEncoder.gen[Interest]
  implicit val decoderInterest: JsonDecoder[Interest] = DeriveJsonDecoder.gen[Interest]
}

case class Interest(name: String, keywords: List[String]) extends CvItem {
  override def summary: String        = ""
  override def features: List[String] = List.empty
  override def company: String        = name
  override def title: String          = keywords.mkString(", ")
  override def timeSpan: String       = ""
}
