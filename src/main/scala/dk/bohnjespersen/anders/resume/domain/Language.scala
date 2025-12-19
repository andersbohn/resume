package dk.andersbohn.resume.domain

import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

object Language {
  implicit val encoderLanguage: JsonEncoder[Language] = DeriveJsonEncoder.gen[Language]
  implicit val decoderLanguage: JsonDecoder[Language] = DeriveJsonDecoder.gen[Language]
}

case class Language(language: String, fluency: String)
