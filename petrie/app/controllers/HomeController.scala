package controllers

import javax.inject.{Inject, Singleton}

import play.api.mvc._

import scala.concurrent.ExecutionContext

@Singleton
class HomeController @Inject() (cc: ControllerComponents)
                               (implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  def index = Action { implicit request =>
      Ok(views.html.index())
  }

}
