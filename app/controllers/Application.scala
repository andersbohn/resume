package controllers

import play.api.mvc._
import play.api.i18n.Messages

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def test = Action {
    Ok(views.html.index("d->"+Messages("description")))
  }

}