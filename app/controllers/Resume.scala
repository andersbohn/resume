package controllers

import com.google.inject.Inject
import play.api.i18n.{Lang, Langs, MessagesApi}
import play.api.mvc._

class Resume @Inject()(implicit langs: Langs, messagesApi: MessagesApi) extends InjectedController with Language {

  def index = withLang { implicit request =>
    Ok(views.html.resume()(Lang("en"), messagesApi))
  }

  def en = withLang { implicit request =>
    Ok(views.html.resume()(Lang("en"), messagesApi))
  }

  def da = withLang { implicit request =>
    Ok(views.html.resume()(Lang("da"), messagesApi))
  }

  def de = withLang { implicit request =>
    Ok(views.html.resume()(Lang("de"), messagesApi))
  }

  
}