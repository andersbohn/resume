package dk.andersbohn.resume.domain

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import dk.andersbohn.resume.domain.Basics.Url
import dk.andersbohn.resume.domain.Work.Formatter
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

object Work {
  implicit val encoderWork: JsonEncoder[Work] = DeriveJsonEncoder.gen[Work]
  implicit val decoderWork: JsonDecoder[Work] = DeriveJsonDecoder.gen[Work]
  val Formatter                               = DateTimeFormatter.ofPattern("YYYY.MM")
}

case class Work(
  summary: String,
  website: Option[Url],
  name: String,
  location: Option[String],
  position: String,
  startDate: LocalDate,
  endDate: LocalDate,
  highlights: List[String] = Nil
) extends CvItem {
  override def features: List[String] = highlights
  override def company: String        = name
  override def title: String          = position
  override def timeSpan: String       = startDate.format(Formatter) + " → " + endDate.format(Formatter)
}
