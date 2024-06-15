package dk.andersbohn.resume.service

import dk.andersbohn.resume.domain.{CvItem, Language, Resume}
import zio.http._
import zio.http.template.Html.raw
import zio.http.template._

import scala.io.{BufferedSource, Codec, Source}

object ResumeHtml {

  def splitEqTuble(s: String)                      = s.split("=").toList match {
    case Nil          => ""   -> ""
    case head :: Nil  => head -> ""
    case head :: tail => head -> tail.mkString(" ")
  }
  private lazy val bufferedSource: BufferedSource  = Source.fromResource("messages")(Codec.UTF8)
  private lazy val nonEmptyLines: Iterator[String] = bufferedSource.getLines().filter(_.contains("="))
  lazy val messages                                = nonEmptyLines.map(splitEqTuble).toMap

  def Messages(key: String): String = messages.getOrElse(key, "N//A")

  case class PersonalInfo(summary: String, features: List[String], company: String, title: String, timeSpan: String)
      extends CvItem

  def languageMap(languages: List[Language]) =
    languages
      .groupBy(_.fluency)
      .toSeq
      .sortBy(_._1)
      .reverse
      .map { case (fluency, languages) =>
        fluency + " " + languages.map(_.language).mkString(" and ")
      }
      .mkString(", ")

  def personalInfo(implicit resume: Resume) = List(
    PersonalInfo("", List.empty, Messages("born_label"), Messages("born"), ""),
    PersonalInfo("", List.empty, Messages("citizenship_label"), Messages("citizenship"), ""),
    PersonalInfo("", List.empty, Messages("status_label"), Messages("status"), ""),
    PersonalInfo("", List.empty, Messages("languages_label"), languageMap(resume.languages), "")
  )

  val Nbsp = raw("&nbsp;")

  def header(extraDivClass: Option[String] = None)(implicit resume: Resume): Dom =
    div(
      classAttr := (s"row ${extraDivClass.mkString("")}"),
      div(classAttr := "span6", h1(classAttr := "logo", resume.basics.name, br(), small(resume.basics.label))),
      div(
        classAttr   := "span3",
        ul(
          classAttr := "contact-info hide-on-phone fixed",
          li("Mail:", Nbsp, a(href := s"mailto:#${resume.basics.email}", resume.basics.email)),
          li(s"${Messages("phone_label")}: ", strong(Messages("phone")))
        )
      ),
      div(classAttr := "span3", img(srcAttr := asset("images/profilfoto1.jpg"), classAttr := "responsive-img profile"))
    )

  def cvItems(list: List[CvItem], headerKey: String)(implicit resume: Resume): List[Dom] =
    div(
      classAttr := "row",
      div(classAttr := "span3", Nbsp),
      div(classAttr := "span9", h3(classAttr := "cv-section-title", Messages(headerKey)))
    ) ::
      list
        .flatMap { cvItem =>
          val features = cvItem.features
          List(
            div(
              classAttr := "row cv-item",
              div(
                classAttr := "span3 cv-item-description",
                h6(cvItem.company),
                p(span(classAttr := "mute", cvItem.timeSpan))
              ),
              div(
                classAttr := "span9 cv-item-details",
                h4(cvItem.title),
                p(cvItem.summary, cvItem.notaBene.toList.flatMap(nb => List(br(), span(classAttr := "note", nb)))),
                (if (features.isEmpty) ""
                 else ul(classAttr := "disc last", features.map(li(_))))
              )
            )
          )
        }

  def asset(name: String) = s"/public/$name"

  def main(ttitle: String, content: Dom*)(implicit resume: Resume): Html =
    html(
      head(
        title(ttitle),
        meta(httpEquivAttr := "Content-Type", contentAttr               := "text/html; charset=utf-8"),
        meta(nameAttr      := "viewport", contentAttr                   := "width=device-width, initial-scale=1, maximum-scale=1"),
        meta(nameAttr := "description", contentAttr := " add description  ... "), //FIXME !!!
        meta(nameAttr      := "keywords", contentAttr                   := " add keywords  ... "),
        link(relAttr       := "shortcut icon", hrefAttr                 := asset("favicon.ico")),
        link(relAttr       := "apple-touch-icon-precomposed", hrefAttr  := asset("apple-touch-icon.png")),
        link(
          typeAttr         := "text/css",
          hrefAttr         := asset("stylesheets/style.css"),
          relAttr          := "stylesheet",
          mediaAttr        := "all"
        ),
        link(
          typeAttr         := "text/css",
          hrefAttr         := asset("stylesheets/main.css"),
          relAttr          := "stylesheet",
          mediaAttr        := "all"
        ),
        link(
          hrefAttr         := "http://fonts.googleapis.com/css?family=Open+Sans:300,400,700",
          relAttr          := "stylesheet",
          typeAttr         := "text/css"
        ),
        script(srcAttr     := asset("js/jquery-1.9.1.min.js"), typeAttr := "text/javascript"),
        script(typeAttr    := "text/javascript", srcAttr                := asset("js/imagebox/jquery.imagebox.min.js")),
        link(relAttr       := "stylesheet", typeAttr                    := "text/css", hrefAttr := asset("js/imagebox/imagebox.css")),
        script(typeAttr    := "text/javascript", srcAttr                := asset("js/flexslider/jquery.flexslider.min.js")),
        link(relAttr       := "stylesheet", typeAttr                    := "text/css", hrefAttr := asset("js/flexslider/flexslider.css")),
        link(relAttr       := "stylesheet", typeAttr                    := "text/css", hrefAttr := asset("js/validity/css.validity.css")),
        script(typeAttr    := "text/javascript", srcAttr                := asset("js/validity/jquery.validity.js")),
        script(typeAttr    := "text/javascript", srcAttr                := asset("js/scripts.js")),
        script(typeAttr    := "text/javascript", srcAttr                := asset("js/plugins.js"))
      ),
      body(content)
    )

  def resume(implicit resume: Resume): Html =
    main(
      "Formal CV",
      div(
        classAttr := "wrap",
        div(
          classAttr := "content",
          header(None),
          div(classAttr := "row", div(classAttr := "span12", h2(classAttr := "cv-profile", resume.basics.summary))),
          cvItems(resume.awards, "assignments_heading")
        )
      ),
      div(
        classAttr := "wrap",
        div(classAttr := "content", header(Some("hide-on-phone")), cvItems(resume.work, "working_experience"))
      ),
      div(
        classAttr := "wrap",
        div(
          classAttr := "content",
          header(Some("hide-on-phone")),
          cvItems(resume.education, "education_section_title"),
          cvItems(resume.interests, "interests_heading")
        )
      )
    )

}
