package dk.andersbohn.resume.service

import java.io.File

import zio._

case class Config(usernames: List[String])

object Config {
//  val descriptor: ConfigDescriptor[Config] =
//    DeriveConfigDescriptor.descriptor[Config]

//  val live: ZLayer[system.System, Nothing, Has[Config]] =
//    TypesafeConfig.fromHoconFile(new File("application.conf"), descriptor).orDie

//  val service: URIO[Has[Config], Config] = ZIO.service[Config]
}
