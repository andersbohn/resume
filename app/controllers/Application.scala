package controllers

import com.google.inject.Inject
import play.api.i18n.{Lang, Langs, MessagesApi}

class Application @Inject()(implicit langs: Langs, messagesApi: MessagesApi) extends Language {

  def index = withLang { implicit request =>
    Ok(views.html.index()(Lang("en"), messagesApi))
  }

  def en = withLang { implicit request =>
    Ok(views.html.index()(Lang("en"), messagesApi))
  }

  def da = withLang { implicit request =>
    Ok(views.html.index()(Lang("da"), messagesApi))
  }

  def ge = withLang { implicit request =>
    Ok(views.html.index()(Lang("ge"), messagesApi))
  }

  def de = withLang { implicit request =>
    Ok(views.html.index()(Lang("de"), messagesApi))
  }

  def alpinDe = withLang { implicit request =>
    Ok(views.html.alpin()(Lang("de"), messagesApi))
  }

}