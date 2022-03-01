package dk.andersbohn.resume.domain

import dk.andersbohn.resume.domain.Award.Formatter
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object Award {
  implicit val encoderAssignment: JsonEncoder[Award] = DeriveJsonEncoder.gen[Award]
  implicit val decoderAssignment: JsonDecoder[Award] = DeriveJsonDecoder.gen[Award]
  val Formatter                                      = DateTimeFormatter.ofPattern("YYYY")
}

case class Award(summary: String, title: String, awarder: String, date: LocalDate, highlights: List[String] = Nil)
    extends CvItem {
  override def features: List[String] = highlights
  override def company: String        = awarder
  override def timeSpan: String       = date.format(Formatter)
}
