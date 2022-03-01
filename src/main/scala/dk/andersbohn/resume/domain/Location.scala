package dk.andersbohn.resume.domain

import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

object Location {
  implicit val encoderLocation: JsonEncoder[Location] = DeriveJsonEncoder.gen[Location]
  implicit val decoderLocation: JsonDecoder[Location] = DeriveJsonDecoder.gen[Location]
}

case class Location(city: String, countryCode: String)
