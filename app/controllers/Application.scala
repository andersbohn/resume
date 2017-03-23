package controllers

import play.api.mvc._
import play.api.i18n.Messages
import play.api.Logger
import play.api.i18n.Lang

object Application extends Language {

  def index = withLang { implicit request =>
    Ok(views.html.index())
  }

  def en = withLang { implicit request =>
    Ok(views.html.index()(Lang("en")))
  }

  def da = withLang { implicit request =>
    Ok(views.html.index()(Lang("da")))
  }

  def ge = withLang { implicit request =>
    Ok(views.html.index()(Lang("ge")))
  }

  def de = withLang { implicit request =>
    Ok(views.html.index()(Lang("de")))
  }

  def alpinDe = withLang { implicit request =>
    Ok(views.html.alpin()(Lang("de")))
  }

}