package controllers

import play.api.mvc._
import play.api.i18n.Messages
import play.api.Logger
import play.api.i18n.Lang


trait Language extends Controller {

  import play.api.Play.current;
  
  def withLang(f: => Request[AnyContent] => Result) = Action { implicit request =>
    val referrer = request.headers.get(REFERER).getOrElse("/")
    println("ref " +referrer+" < " +REFERER+" << ")
    request.headers.toMap.foreach(println)
    request.getQueryString("lang").flatMap(Lang.get(_)) match {
      case Some(lang) => Redirect(referrer).withLang(lang)
      case None => f(request)
    }
  }
}