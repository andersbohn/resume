package dk.andersbohn.resume.domain

trait CvItem {
  def summary: String
  def features: List[String]
  def company: String
  def title: String
  def timeSpan: String
  def notaBene: Option[String] = None
}
