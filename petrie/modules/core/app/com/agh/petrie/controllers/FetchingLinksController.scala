package com.agh.petrie.controllers

import javax.inject.{Inject, Singleton}

import com.agh.petrie.model.view.FetchLinksRequest
import play.api.libs.json.{JsError, Json}
import play.api.mvc._
import FetchLinksRequest._
import com.agh.petrie.repositories.RequestHistoryRepository
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FetchingLinksController @Inject()(
  cc: ControllerComponents,
  requestHistoryRepository: RequestHistoryRepository,
  dbConfigProvider: DatabaseConfigProvider,
)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def fetchLinks = Action.async(parse.json) { implicit  request =>
    request.body.validate[FetchLinksRequest].fold(
      errors => Future {BadRequest(Json.obj("status" ->"KO", "message" -> JsError.toJson(errors)))},
      fetchLinksRequest => {
        dbConfigProvider.get.db.run(requestHistoryRepository.save(fetchLinksRequest)).map(_ => Ok(Json.obj("status" ->"OK")))
      }
    )
  }

}
