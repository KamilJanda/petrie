package agh.petrie.scraping.actors.controllers

import agh.petrie.scraping.actors.controllers.FrontierPriorityQueue.UrlNode
import agh.petrie.scraping.actors.receptionist.SimpleReceptionist.WebsiteData
import agh.petrie.scraping.api.BasicScrapingApi.{Complete, Message}
import agh.petrie.scraping.model.Configuration
import agh.petrie.scraping.service.ThrottlingService.ScheduledVisitJournal
import agh.petrie.scraping.service.{ScraperResolverService, ThrottlingService}
import akka.actor.{ActorRef, PoisonPill, Props}

class StreamingController(
  scraperResolverService: ScraperResolverService,
  throttlingService: ThrottlingService,
  configuration: Configuration,
  webSocketActor: ActorRef
) extends BaseController(scraperResolverService, throttlingService, configuration) {
  override def onNegativeDepth = stopFetching

  override def onCheckDone(
    scheduledVisitJournal: ScheduledVisitJournal,
    queuedUrls: Set[UrlNode],
    children: Set[ActorRef],
    websitesData: Set[WebsiteData],
    websiteData: WebsiteData,
    responseTo: ActorRef
  ): Unit = {
    webSocketActor ! Message(websiteData)
    if ((children - sender).isEmpty && queuedUrls.isEmpty) {
      stopFetching
    } else {
      context.become(checkingUrls(scheduledVisitJournal, queuedUrls, children - sender, websitesData, responseTo))
    }
  }

  private def stopFetching = {
    webSocketActor ! Complete
    webSocketActor ! PoisonPill
    context.stop(self)
  }

}

object StreamingController {
  def props(
    scraperResolverService: ScraperResolverService,
    throttlingService: ThrottlingService,
    configuration: Configuration,
    webSocketActor: ActorRef
  ) =
    Props(new StreamingController(scraperResolverService, throttlingService, configuration, webSocketActor))
}
