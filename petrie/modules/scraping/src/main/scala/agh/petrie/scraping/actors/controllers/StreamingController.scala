package agh.petrie.scraping.actors.controllers

import agh.petrie.scraping.actors.controllers.BaseController.{CheckDone, ScrapFromUrl}
import agh.petrie.scraping.api.BasicScrapingApi.{Complete, Message}
import agh.petrie.scraping.model.Configuration
import agh.petrie.scraping.service.ScraperResolverService
import akka.actor.{ActorRef, PoisonPill, Props}

class StreamingController(
  scraperResolverService: ScraperResolverService,
  configuration: Configuration,
  webSocketActor: ActorRef
) extends BaseController(scraperResolverService, configuration) {
  override def onNegativeDepth = stopFetching

  override def onFetchedUrl(url: String, responseTo: ActorRef) = {
    webSocketActor ! Message(url)
  }

  override def onCheckDone(children: Set[ActorRef], urls: Set[String], responseTo: ActorRef) = {
    if ((children - sender).isEmpty) {
      stopFetching
    } else {
      context.become(checkingUrls(children - sender, urls, responseTo))
    }
  }

  private def stopFetching = {
    webSocketActor ! Complete
    webSocketActor ! PoisonPill
    context.stop(self)
  }

}

object StreamingController {
  def props(scraperResolverService: ScraperResolverService, configuration: Configuration, webSocketActor: ActorRef) =
    Props(new StreamingController(scraperResolverService, configuration, webSocketActor))
}
