package dk.andersbohn.resume.service.gist

import dk.andersbohn.resume.domain.Resume
import zio.{Task, ZIO}

import dk.bohnjespersen.anders.resume.domain.types.Lang
import dk.bohnjespersen.anders.resume.service.Messages

object Gist extends Serializable {
  trait Service extends Serializable {
    def resume(lang: Lang, patchName: Option[String]): Task[Resume]
    def messages(lang: Lang): Task[Messages]
  }

  def resume(lang: Lang, patchName: Option[String]): ZIO[Gist.Service, Throwable, Resume] =
    ZIO.serviceWithZIO[Gist.Service](_.resume(lang, patchName))

  def messages(lang: Lang): ZIO[Gist.Service, Throwable, Messages] =
    ZIO.serviceWithZIO[Gist.Service](_.messages(lang))
}
