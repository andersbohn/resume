package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.i18n.Lang

object Choose extends Controller {

  import play.api.Play.current

  val localeForm = Form("locale" -> nonEmptyText)

  val changeLocale = Action {
    implicit request =>
      val referrer = request.headers.get(REFERER).getOrElse(HOME_URL)
      localeForm.bindFromRequest.fold(
        errors => {
          Logger.logger.debug("The locale can not be change to : " + errors.get)
          BadRequest(referrer)
        },
        locale => {
          Logger.logger.debug("Change user lang to : " + locale)
          Redirect(referrer).withLang(Lang(locale)) // TODO Check if the lang is handled by the application
        })
  }


  val HOME_URL = routes.Choose.index().url

  def index = Action { implicit request =>
//    Cached {
      Ok(views.html.index(""))
//    }
  }


}
