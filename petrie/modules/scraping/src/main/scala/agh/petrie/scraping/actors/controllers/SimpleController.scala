package agh.petrie.scraping.actors.controllers

import agh.petrie.scraping.actors.controllers.FrontierPriorityQueue.UrlNode
import agh.petrie.scraping.actors.receptionist.SimpleReceptionist
import agh.petrie.scraping.actors.receptionist.SimpleReceptionist.{FetchedData, WebsiteData}
import agh.petrie.scraping.model.Configuration
import agh.petrie.scraping.service.ThrottlingService.ScheduledVisitJournal
import agh.petrie.scraping.service.{ScraperResolverService, ThrottlingService}
import akka.actor.{ActorRef, Props}

class SimpleController(
  scraperResolverService: ScraperResolverService,
  throttlingService: ThrottlingService,
  configuration: Configuration
) extends BaseController(scraperResolverService, throttlingService, configuration) {

  override def onNegativeDepth(): Unit =
    sender ! FetchedData(Set())

  override def onCheckDone(
    scheduledVisitJournal: ScheduledVisitJournal,
    queuedUrls: Set[UrlNode],
    children: Set[ActorRef],
    websitesData: Set[WebsiteData],
    websiteData: WebsiteData,
    responseTo: ActorRef
  ): Unit = {
    if ((children - sender).isEmpty && queuedUrls.isEmpty) {
      responseTo ! SimpleReceptionist.FetchedData(websitesData)
      context.stop(self)
    } else {
      context.become(checkingUrls(scheduledVisitJournal, queuedUrls, children - sender, websitesData, responseTo))
    }
  }
}

object SimpleController {
  def props(
    scraperResolverService: ScraperResolverService,
    throttlingService: ThrottlingService,
    configuration: Configuration
  ) =
    Props(new SimpleController(scraperResolverService, throttlingService, configuration))
}
