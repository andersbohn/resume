package dk.andersbohn.resume.domain
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

object Reference {
  implicit val encoderReference: JsonEncoder[Reference] = DeriveJsonEncoder.gen[Reference]
  implicit val decoderReference: JsonDecoder[Reference] = DeriveJsonDecoder.gen[Reference]
}

case class Reference(reference: String, name: String)
