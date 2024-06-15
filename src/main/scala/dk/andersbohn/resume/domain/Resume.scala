package dk.andersbohn.resume.domain

import zio.json._

import java.time.{Instant, LocalDateTime}

object Resume {
  implicit val encoderResume: JsonEncoder[Resume] = DeriveJsonEncoder.gen[Resume]
  implicit val decoderResume: JsonDecoder[Resume] = DeriveJsonDecoder.gen[Resume]
}

case class Resume(
  basics: Basics,
  languages: List[Language],
  education: List[Education],
  references: List[Reference],
  skills: List[Skill],
  awards: List[Award],
  work: List[Work],
  interests: List[Interest]
)
