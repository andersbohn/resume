package dk.andersbohn.resume.domain

import java.time.LocalDate
import zio.json.javatime._

import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

object Education {
  implicit val encoderEducation: JsonEncoder[Education] = DeriveJsonEncoder.gen[Education]
  implicit val decoderEducation: JsonDecoder[Education] = DeriveJsonDecoder.gen[Education]
}

case class Education(endDate: LocalDate, startDate: LocalDate, area: String, studyType: String, institution: String)
    extends CvItem {
  override def summary: String        = studyType
  override def features: List[String] = List.empty
  override def company: String        = institution
  override def title: String          = area
  override def timeSpan: String       = f"$startDate-$endDate"
}
