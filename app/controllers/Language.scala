package controllers

import play.api.i18n._
import play.api.mvc._


trait Language extends InjectedController with I18nSupport {

  def withLang(f: => Request[AnyContent] => Result) = Action { implicit request =>
    val referrer = request.headers.get(REFERER).getOrElse("/")
    println("ref " + referrer + " < " + REFERER + " << ")
    request.headers.toMap.foreach(println)
    request.getQueryString("lang").flatMap(Lang.get(_)) match {
      case Some(lang) => Redirect(referrer).withLang(lang)
      case None => f(request)
    }
  }
}