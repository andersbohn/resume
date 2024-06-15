package dk.andersbohn.resume.domain

import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

object Birth {
  implicit val encoderBirth: JsonEncoder[Birth] = DeriveJsonEncoder.gen[Birth]
  implicit val decoderBirth: JsonDecoder[Birth] = DeriveJsonDecoder.gen[Birth]
}

case class Birth(place: String, state: String, date: Long)
