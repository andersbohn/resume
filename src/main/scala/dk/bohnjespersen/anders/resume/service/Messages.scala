package dk.bohnjespersen.anders.resume.service

trait Messages  {
  def mfk(key: String): String        = messageForKey(key).mkString(", ")
  def mfks(key: String): List[String] = messageForKey(key)
  def messageForKey(key: String): List[String]
}
object Messages {
  def forMap(messages: Map[String, List[String]]): Messages = new Messages {
    override def messageForKey(key: String): List[String] =
      messages.getOrElse(key, List(s"N/A $key"))
  }
}
