package agh.petrie.core.controllers

import agh.petrie.core.WebScraperProvider
import agh.petrie.core.model.view.FetchLinksRequest
import agh.petrie.core.model.view.FetchLinksRequest._
import agh.petrie.core.model.view.FetchedDataView._
import agh.petrie.core.model.view.WebSocketFormat._
import agh.petrie.core.repositories.RequestHistoryRepository
import agh.petrie.core.services.ConfigurationValidationService
import agh.petrie.core.viewconverters.WebsocketConverterActor.GetUrlsView
import agh.petrie.core.viewconverters.{ConfigurationViewConverter, FetchedUrlsViewConverter, WebsocketConverterActor}
import agh.petrie.scraping.actors.receptionist.StreamingReceptionist.GetUrls
import agh.petrie.scraping.api.BasicScrapingApi.Protocol
import akka.actor.ActorSystem
import akka.util.Timeout
import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.{JsError, Json}
import play.api.libs.streams.ActorFlow
import play.api.mvc.{AbstractController, ControllerComponents, WebSocket}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FetchingLinksController @Inject()(
  cc: ControllerComponents,
  requestHistoryRepository: RequestHistoryRepository,
  dbConfigProvider: DatabaseConfigProvider,
  webScraperProvider: WebScraperProvider,
  fetchedUrlsViewConverter: FetchedUrlsViewConverter,
  configurationViewConverter: ConfigurationViewConverter,
  configurationValidationService: ConfigurationValidationService
)(implicit ec: ExecutionContext, materialize: akka.stream.Materializer, as: ActorSystem)
  extends AbstractController(cc) {

  def fetchLinks = Action.async(parse.json) { implicit request =>
    implicit val timeout = Timeout(3600 seconds)
    request.body
      .validate[FetchLinksRequest]
      .fold(
        errors => Future { BadRequest(Json.obj("status" -> "KO", "message" -> JsError.toJson(errors))) },
        fetchLinksRequest => {
          configurationValidationService.validate(fetchLinksRequest.configuration) match {
            case Left(message) => Future { BadRequest(Json.toJson(message)) }
            case _ =>
              for {
                _ <- dbConfigProvider.get.db.run(requestHistoryRepository.save(fetchLinksRequest))
                configuration = configurationViewConverter.fromView(fetchLinksRequest.configuration, false)
                fetched <- webScraperProvider.get.getAllLinks(fetchLinksRequest.url, configuration)
                fetchedView = fetchedUrlsViewConverter.toView(fetched)
              } yield Ok(Json.toJson(fetchedView))
          }
        }
      )
  }

  def fetchLinksTest = Action.async(parse.json) { implicit request =>
    implicit val timeout = Timeout(480 seconds)
    request.body
      .validate[FetchLinksRequest]
      .fold(
        errors => Future { BadRequest(Json.obj("status" -> "KO", "message" -> JsError.toJson(errors))) },
        fetchLinksRequest => {
          configurationValidationService.validate(fetchLinksRequest.configuration) match {
            case Left(message) => Future { BadRequest(Json.toJson(message)) }
            case _ =>
              for {
                _ <- dbConfigProvider.get.db.run(requestHistoryRepository.save(fetchLinksRequest))
                configuration = configurationViewConverter.fromView(fetchLinksRequest.configuration, true)
                fetched <- webScraperProvider.get.getAllLinks(fetchLinksRequest.url, configuration)
                fetchedView = fetchedUrlsViewConverter.toView(fetched)
              } yield Ok(Json.toJson(fetchedView))
          }
        }
      )
  }

  def fetchLinksStream = WebSocket.accept[GetUrlsView, Protocol] { _ =>
    ActorFlow.actorRef { out =>
      WebsocketConverterActor.props(as.actorOf(webScraperProvider.get.fetchLinksAsync(out)), configurationViewConverter)
    }
  }

}
