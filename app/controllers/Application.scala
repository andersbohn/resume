package controllers

import play.api.mvc._
import play.api.i18n.Messages
import play.api.Logger
import play.api.i18n.Lang

object Application extends Controller {

  def index = withLang { implicit request =>
    Ok(views.html.index())
  }

  def en = withLang { implicit request =>
    Ok(views.html.index()(Lang("en")))
  }

  def da = withLang { implicit request =>
    Ok(views.html.index()(Lang("da")))
  }

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