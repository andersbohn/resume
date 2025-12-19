package dk.andersbohn.resume.service.gist

import java.io.InputStream
import java.security.KeyStore
import javax.net.ssl.TrustManagerFactory

import scala.io.Source

import dk.andersbohn.resume.domain.Resume
import dk.andersbohn.resume.service.gist.GistLive.GistUrl
import io.netty.handler.ssl.SslContextBuilder
import zio.*
import zio.http.*
import zio.http.netty.client.NettyClientDriver
import zio.json.*
import zio.json.ast.Json

import dk.bohnjespersen.anders.resume.domain.types.Lang
import dk.bohnjespersen.anders.resume.service.Messages
object GistLive {
  val GistUrl =
    "https://gist.githubusercontent.com/andersbohn/2d7c3b56d2c6993da2b178a25fb1503d/raw/resume.json"

  val headers = Headers("host", "gist.githubusercontent.com")

  val trustStore: KeyStore        = KeyStore.getInstance("JKS")
  val trustStorePath: InputStream = getClass.getClassLoader.getResourceAsStream("truststore.jks")
  val trustStorePassword: String  = "changeit"
  val trustManagerFactory: TrustManagerFactory =
    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm)

  trustStore.load(trustStorePath, trustStorePassword.toCharArray)
  trustManagerFactory.init(trustStore)

  val sslConfig =
    ClientSSLConfig.FromTrustStoreResource(
      trustStorePath = "truststore.jks",
      trustStorePassword = "changeit",
    )

//  val clientConfig = ClientConfig.empty.ssl(sslConfig)

  val live     = ZLayer.succeed(GistLiveImpl)
  val resource = ZLayer.succeed(GistResImpl)
}

case object GistLiveImpl extends Gist.Service {
  override def resume(lang: Lang, patchName: Option[String]): Task[Resume] = {
    val value = for {
      url    <- ZIO.fromEither(URL.decode(GistUrl))
      client <- ZIO.service[Client]
      res    <- client.url(url).get("/")
      data   <- res.body.asString
      resume <- ZIO.fromEither(
        data.fromJson[Resume].left.map(s => new RuntimeException(s"error -> $s")),
      )
    } yield resume
    value.provide(Client.default, Scope.default)
  }

  override def messages(lang: Lang): Task[Messages] = ???
}
case object GistResImpl  extends Gist.Service {
  private def mergeOn(en: Resume, patchOpt: Option[Json]): Either[String, Resume] =
    patchOpt match {
      case Some(patch) =>
        for {
          enJson <- en.toJsonAST
          merged = enJson.merge(patch)
          res <- merged.as[Resume]
        } yield res
      case None        => Right(en)
    }

  private def zioResource(path: String): Task[String] = for {
    str <- ZIO
      .fromOption(Option(Source.fromResource(path)))
      .mapError(_ => new RuntimeException(s"Resource $path not found"))
  } yield str.getLines().mkString("\n")

  override def resume(lang: Lang, patchName: Option[String]): Task[Resume] =
    for {
      strEn     <- zioResource("./resume.json")
      patchLang <- lang match {
        case Lang.en => ZIO.none
        case Lang.de => zioResource("./resume_de.json").map(Some(_))
      }
      patchOpt  <- patchName.map(n => zioResource(s"./$n")).map(_.map(Some(_))).getOrElse(ZIO.none)

      resume <- ZIO.fromEither {
        (for {
          en <- strEn.fromJson[Resume]
          ln <- patchLang match {
            case Some(p) => p.fromJson[Json].map(Some(_))
            case None    => Right(None)
          }
          po <- patchOpt match {
            case Some(p) => p.fromJson[Json].map(Some(_))
            case None    => Right(None)
          }

          res1 <- mergeOn(en, ln)
          res  <- mergeOn(res1, po)
        } yield res).left.map { e =>
          new RuntimeException(s"error -> $e")
        }
      }
    } yield resume

  override def messages(lang: Lang): Task[Messages] =
    for {
      data <- ZIO.attempt {
        val source  = Source.fromResource(s"./messages_$lang.json")
        val strings = source.getLines().mkString("\n")
        strings
      }
      msgs <- ZIO.fromEither {
        val value = data.fromJson[Map[String, List[String]]]
        value.left.map { s =>
          new RuntimeException(s"error -> $s")
        }
      }
    } yield Messages.forMap(msgs)
}
