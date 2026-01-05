package dk.andersbohn.resume.service

import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDateTime, ZoneId, ZonedDateTime}

import dk.andersbohn.resume.domain.{CvItem, Interest, Resume}
import zio.*
import zio.http.*
import zio.http.template.{footer, *}

import dk.bohnjespersen.anders.resume.service.Messages

object ResumeHtml {
  val flags = Map("Danish" -> "🇩🇰", "English" -> "🇬🇧", "German" -> "🇩🇪", "French" -> "🇫🇷")

  private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

  val DekoImages = Map(
    "Metaco/Ripple" -> List(
//      "images/metaco-logo.jpeg",
      "/assets/images/ripple-logo-color.png",
    ),
  )

  val Nbsp = span("\u00A0")

  private def contactInfo()(implicit resume: Resume, messages: Messages) = {
    val lst = List(
      p(
        a(hrefAttr := "mailto:#", resume.basics.email),
        Nbsp,
        span("-"),
        Nbsp,
        span(messages.mfk("phone_label")),
        Nbsp,
        span(messages.mfk("phone")),
        Nbsp,
        span(messages.mfk("webcv_label")),
      ),
    )

    div(classAttr := "contact-info hide-on-phone fixed", lst)
  }

  def header(
      extraDivClass: Option[String] = None,
  )(implicit resume: Resume, messages: Messages): Dom =
    div(
      classAttr := s"row ${extraDivClass.mkString}",
      div(
        classAttr := "span7",
        h1(
          classAttr := "logo",
          resume.basics.name,
          br(),
          small(resume.basics.label),
        ),
      ),
      div(
        classAttr := "span3",
        ul(
          li(messages.mfk("phone")),
          li(
            a(
              hrefAttr := resume.basics.website,
              resume.basics.website,
            ),
          ),
          li(
            resume
              .basics
              .profiles
              .find(_.network == "github")
              .map(_.url)
              .map(ghurl =>
                a(
                  hrefAttr := ghurl,
                  ghurl,
                ),
              )
              .getOrElse(Nbsp),
          ),
          li(
            resume
              .basics
              .profiles
              .find(_.network == "linkedin")
              .map(_.url)
              .map(liurl =>
                a(
                  hrefAttr := liurl,
                  "linkedin.com/in/andersbohn",
                ),
              )
              .getOrElse(Nbsp),
          ),
        ),
      ),
      div(
        classAttr := "span2",
        img(
          classAttr := "responsive-img profile",
          altAttr   := "",
          srcAttr   := "/assets/images/profilfoto1.jpg",
        ),
      ),
    )

  def makeFooter(
      gitRef: String,
      extraDivClass: Option[String] = None,
  ): Dom = div(
    classAttr := s"row ${extraDivClass.mkString}",
    div(
      classAttr := "span4",
      Nbsp,
    ),
    div(
      classAttr := "span4",
      footer(
        i(s"$gitRef", classAttr := "version-tag"),
      ),
    ),
    div(
      classAttr := "span4",
      Nbsp,
    ),
  )

  def cvItems(list: List[CvItem], headerKey: String)(implicit
      resume: Resume,
      messages: Messages,
  ): List[Dom] =
    div(
      classAttr := "row",
      div(classAttr := "span2", Nbsp),
      div(
        classAttr   := "span10",
        h3(classAttr := "cv-section-title", messages.mfk(headerKey)),
      ),
    ) ::
      list.flatMap { cvItem =>
        val features = cvItem.features
        List(
          div(
            classAttr := "row cv-item",
            div(
              classAttr := "span2 cv-item-description",
              h6(cvItem.company),
              p(span(classAttr := "mute", cvItem.timeSpan)),
            ),
            div(
              classAttr := "span10 cv-item-details",
              if (cvItem.title.trim.nonEmpty) h4(cvItem.title) else span(""),
              p(
                cvItem.summary,
                cvItem
                  .notaBene
                  .toList
                  .flatMap(nb => List(br(), span(classAttr := "note", nb))),
              ),
              if (features.isEmpty) ""
              else ul(classAttr := "disc last", features.map(li(_))),
            ),
          ),
        )
      }

  def assets(name: String) = s"/assets$name"

  def main(ttitle: String, content: Dom*)(implicit resume: Resume): Html =
    html(
      head(
        title(ttitle),
        meta(charsetAttr := "utf-8"),
        meta(nameAttr    := "viewport", contentAttr    := "width=device-width, initial-scale=1.0"),
        meta(nameAttr    := "description", contentAttr := " add description  ... "), // FIXME !!!
        meta(nameAttr    := "keywords", contentAttr    := " add keywords  ... "),
        link(relAttr     := "shortcut icon", hrefAttr  := assets("assets/favicon.ico")),
        link(relAttr := "apple-touch-icon-precomposed", hrefAttr := "assets/apple-touch-icon.png"),
        link(
          typeAttr   := "text/css",
          hrefAttr   := assets("/css/base.css"),
          relAttr    := "stylesheet",
          mediaAttr  := "all",
        ),
        link(
          typeAttr   := "text/css",
          hrefAttr   := assets("/css/grid.css"),
          relAttr    := "stylesheet",
          mediaAttr  := "all",
        ),
        link(
          typeAttr   := "text/css",
          hrefAttr   := assets("/css/layout.css"),
          relAttr    := "stylesheet",
          mediaAttr  := "all",
        ),
      ),
      body(content),
    )

  def resume(implicit resume: Resume, messages: Messages, deko: Boolean): Html = {
    val gitRef               = sys
      .env
      .get("GITHUB_REF_NAME")
      .orElse(sys.env.get("GITHUB_SHA").map(_.take(7)))
      .getOrElse("local-dev " + ZonedDateTime.now(ZoneId.of("Zulu")).format(dateTimeFormatter))
    val weDropTake           = messages.mfks("working_experience_page1_drop_take").map(_.toInt)
    val (dropCount, takeCnt) = weDropTake(0) -> weDropTake(1)
    val personalAsInterests  = List(
      Interest(
        messages.mfk("skills_label"),
        messages.mfks("skills_list"),
      ),
      Interest(
        messages.mfk("citizenship_label"),
        messages.mfks("citizenship_summary"),
      ),
      Interest(
        messages.mfk("languages_label"),
        List(resume.languages.map(l => l._2 + " " + l._1).mkString(" - ")),
      ),
    )
    main(
      "Resume Anders Bohn Jespersen",
      div(
        classAttr := "wrap",
        div(
          classAttr := "content",
          header(None),
          div(
            classAttr := "row",
            div(
              classAttr := "span12",
              h2(classAttr := "cv-profile", resume.basics.summary),
            ),
          ),
          cvItems(personalAsInterests ::: resume.interests, "interests_heading"),
          makeFooter(gitRef, Some("hide-on-phone")),
        ),
      ),
      div(
        classAttr := "wrap",
        div(
          classAttr := "content",
          header(Some("hide-on-phone")),
          cvItems(resume.work.dropRight(dropCount), "working_experience_heading"),
          makeFooter(gitRef, Some("hide-on-phone")),
        ),
      ),
    )
  }
}
