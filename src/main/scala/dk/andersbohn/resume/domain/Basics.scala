package dk.andersbohn.resume.domain
import dk.andersbohn.resume.domain.Basics.Url
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

object Basics {
  implicit val encoderBasics: JsonEncoder[Basics] = DeriveJsonEncoder.gen[Basics]
  implicit val decoderBasics: JsonDecoder[Basics] = DeriveJsonDecoder.gen[Basics]

  type Url = String // FIXME better zio-json-url-type?
}

case class Basics(
  name: String,
  label: String,
  image: Url,
  summary: String,
  website: Url,
  email: Url,
  location: Location,
  birth: Birth,
  profiles: List[Profile]
)
