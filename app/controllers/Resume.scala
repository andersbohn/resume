package controllers

import play.api.mvc._
import play.api.i18n.Messages
import play.api.Logger
import play.api.i18n.Lang

object Resume extends Controller with Language {

  def index = withLang { implicit request =>
    Ok(views.html.resume())
  }

  def en = withLang { implicit request =>
    Ok(views.html.resume()(Lang("en")))
  }

  def da = withLang { implicit request =>
    Ok(views.html.resume()(Lang("da")))
  }

  def de = withLang { implicit request =>
    Ok(views.html.resume()(Lang("de")))
  }

  
}