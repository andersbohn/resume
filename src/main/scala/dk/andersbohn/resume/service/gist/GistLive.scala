package dk.andersbohn.resume.service.gist

import dk.andersbohn.resume.domain.Resume
import io.netty.handler.ssl.SslContextBuilder
import zio._
import zio.json._

import java.io.InputStream
import java.security.KeyStore
import javax.net.ssl.TrustManagerFactory
import io.netty.handler.ssl.SslContextBuilder
import zio.http.Client

import scala.io.Source

//import zio.http.ClientSSLHandler.ClientSSLOptions
import zio.http._
import zio._

import java.io.InputStream
import java.security.KeyStore
import javax.net.ssl.TrustManagerFactory
import zio.http._
import zio.http.netty.client.NettyClientDriver
object GistLive {

  //val layer = ZIO.succeed(GistLive.apply _)//.toLayer[Gist.Service]

  val GistUrl = "https://gist.githubusercontent.com/andersbohn/2d7c3b56d2c6993da2b178a25fb1503d/raw/resume.json"
//  val GistUrl ="http://localhost:8090/public/resume.json"

  val headers = Headers("host", "gist.githubusercontent.com")

  // Configuring Truststore for https(optional)
  val trustStore: KeyStore                     = KeyStore.getInstance("JKS")
  val trustStorePath: InputStream              = getClass.getClassLoader.getResourceAsStream("truststore.jks")
  val trustStorePassword: String               = "changeit"
  val trustManagerFactory: TrustManagerFactory =
    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm)

  trustStore.load(trustStorePath, trustStorePassword.toCharArray)
  trustManagerFactory.init(trustStore)

  val sslConfig =
    ClientSSLConfig.FromTrustStoreResource(trustStorePath = "truststore.jks", trustStorePassword = "changeit")

//  val clientConfig = ClientConfig.empty.ssl(sslConfig)

  val live     = ZLayer.succeed(GistLiveImpl)
  val resource = ZLayer.succeed(GistResImpl)
}

case object GistLiveImpl extends Gist.Service {
  import GistLive._

  override def theJson: Task[Resume] = {
    val value = for {
      url    <- ZIO.fromEither(URL.decode(GistUrl))
      client <- ZIO.service[Client]
      res <- client.url(url).get("/") //.headers(headers)
      data <- res.body.asString //.succeed(stringFor(res.content))
      resume <- ZIO.fromEither(data.fromJson[Resume].left.map(s => new RuntimeException(s"teherr -> $s")))
    } yield resume
    value.provide(Client.default, Scope.default)
  } //

}
case object GistResImpl extends Gist.Service {

  override def theJson: Task[Resume] =
    for {
      data   <- ZIO.succeed(Source.fromResource("./public/resume.json").getLines().mkString("\n"))
      resume <- ZIO.fromEither(data.fromJson[Resume].left.map(s => new RuntimeException(s"teherr -> $s")))
    } yield resume

}
