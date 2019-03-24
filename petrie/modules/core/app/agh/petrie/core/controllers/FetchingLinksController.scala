package agh.petrie.core.controllers

import javax.inject.{Inject, Singleton}

import agh.petrie.core.model.view.FetchLinksRequest
import play.api.libs.json.{JsError, Json}
import play.api.mvc._
import agh.petrie.core.model.view.FetchLinksRequest._
import agh.petrie.core.model.view.FetchedUrlsView._
import agh.petrie.core.repositories.RequestHistoryRepository
import agh.petrie.scraping.WebScraper
import akka.util.Timeout
import scala.concurrent.duration._
import play.api.db.slick.DatabaseConfigProvider
import agh.petrie.core.viewconverters.FetchedUrlsViewConverter

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FetchingLinksController @Inject()(
  cc: ControllerComponents,
  requestHistoryRepository: RequestHistoryRepository,
  dbConfigProvider: DatabaseConfigProvider,
  webScraper: WebScraper,
  fetchedUrlsViewConverter: FetchedUrlsViewConverter
)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  implicit val timeout = Timeout(3 seconds)

  def fetchLinks = Action.async(parse.json) { implicit  request =>
    request.body.validate[FetchLinksRequest].fold(
      errors => Future {BadRequest(Json.obj("status" ->"KO", "message" -> JsError.toJson(errors)))},
      fetchLinksRequest => {
        for {
          _ <- dbConfigProvider.get.db.run(requestHistoryRepository.save(fetchLinksRequest))
          fetched <- webScraper.getAllLinks(fetchLinksRequest.url, fetchLinksRequest.depth)
          fetchedView = fetchedUrlsViewConverter.toView(fetched)
        } yield  Ok(Json.toJson(fetchedView))
      }
    )
  }

}
