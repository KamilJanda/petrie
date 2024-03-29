package agh.petrie.scraping.actors.receptionist

import agh.petrie.scraping.actors.controllers.BaseController.StartScraping
import agh.petrie.scraping.actors.controllers.StreamingController
import agh.petrie.scraping.actors.receptionist.StreamingReceptionist.GetUrls
import agh.petrie.scraping.model.Configuration
import agh.petrie.scraping.service.{ScraperResolverService, ThrottlingService, UrlPriorityService}
import akka.actor.{Actor, ActorRef, Props}

class StreamingReceptionist(
  scraperResolverService: ScraperResolverService,
  throttlingService: ThrottlingService,
  urlPriorityService: UrlPriorityService,
  socket: ActorRef
) extends Actor {

  override def receive: Receive = {
    case GetUrls(url, configuration) =>
      stream(url, configuration.maxSearchDepth, configuration)
  }

  def stream(
    rootUrl: String,
    depth: Int,
    configuration: Configuration
  ) = {
    val controller =
      context.actorOf(
        StreamingController.props(scraperResolverService, throttlingService, urlPriorityService, configuration, socket)
      )
    controller ! StartScraping(rootUrl)
  }
}

object StreamingReceptionist {

  def props(
    scraperResolverService: ScraperResolverService,
    throttlingService: ThrottlingService,
    urlPriorityService: UrlPriorityService
  )(socket: ActorRef) =
    Props(new StreamingReceptionist(scraperResolverService, throttlingService, urlPriorityService, socket))

  case class GetUrls(rootUrl: String, configuration: Configuration)
}
