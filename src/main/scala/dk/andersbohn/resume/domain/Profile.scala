package dk.andersbohn.resume.domain
import dk.andersbohn.resume.domain.Basics.Url
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

object Profile {
  implicit val encoderProfile: JsonEncoder[Profile] = DeriveJsonEncoder.gen[Profile]
  implicit val decoderProfile: JsonDecoder[Profile] = DeriveJsonDecoder.gen[Profile]
}

case class Profile(url: Url, username: String, network: String)
