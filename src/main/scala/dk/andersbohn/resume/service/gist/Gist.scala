package dk.andersbohn.resume.service.gist

import dk.andersbohn.resume.domain.Resume
import zio.{Task, ZIO}

object Gist extends Serializable {
  trait Service extends Serializable {
    def theJson: Task[Resume]
  }

  def theJson: ZIO[Gist.Service, Throwable, Resume] =
    ZIO.serviceWithZIO[Gist.Service](_.theJson)
}
