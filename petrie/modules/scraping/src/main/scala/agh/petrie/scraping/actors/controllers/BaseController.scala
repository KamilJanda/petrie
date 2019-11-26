package agh.petrie.scraping.actors.controllers

import java.net.URL

import agh.petrie.scraping.actors.controllers.BaseController.{AddUrl, CheckDone, ScrapFromUrl, StartScraping}
import agh.petrie.scraping.actors.controllers.FrontierPriorityQueue.{AddUrlToQueue, UrlNode}
import agh.petrie.scraping.actors.receptionist.SimpleReceptionist.WebsiteData
import agh.petrie.scraping.model.{Configuration, ScrapingScenario}
import agh.petrie.scraping.service.ThrottlingService.ScheduledVisitJournal
import agh.petrie.scraping.service.{ScraperResolverService, ThrottlingService, UrlPriorityService}
import akka.actor.{Actor, ActorRef}

abstract class BaseController(
  scraperResolverService: ScraperResolverService,
  throttlingService: ThrottlingService,
  urlPriorityService: UrlPriorityService,
  configuration: Configuration
) extends Actor {

  private implicit val exec = context.dispatcher

  private val frontierPriorityQueue: ActorRef =
    context.actorOf(FrontierPriorityQueue.props(self).withDispatcher("prio-dispatcher"))

  def onNegativeDepth(): Unit
  def onCheckDone(
    scheduledVisitJournal: ScheduledVisitJournal,
    queuedUrls: Set[UrlNode],
    children: Set[ActorRef],
    websitesData: Set[WebsiteData],
    fetched: WebsiteData,
    responseTo: ActorRef
  ): Unit

  override def receive: Receive = {
    case StartScraping(url) if configuration.maxSearchDepth >= 0 =>
      context.become(checkingUrls(ScheduledVisitJournal.empty, Set.empty, Set.empty, Set.empty, sender))
      configuration.rootScenario.foreach(scenario => {
        self ! AddUrl(url, configuration.maxSearchDepth, Some(scenario))
      })

    case _: StartScraping => onNegativeDepth()
  }

  def checkingUrls(
    scheduledVisitJournal: ScheduledVisitJournal,
    queuedUrls: Set[UrlNode],
    children: Set[ActorRef],
    websitesData: Set[WebsiteData],
    responseTo: ActorRef
  ): Receive = {

    case AddUrl(url, depth, scenario) if depth >= 0 && resolveIfAlreadyScraped(websitesData, url, scenario) =>
      val urlNode        = UrlNode(url, depth, scenario)
      val host           = new URL(url).getHost
      val updatedJournal = throttlingService.updateVisitJournal(host, scheduledVisitJournal)
      val delay          = throttlingService.getDelay(host, updatedJournal)
      val priority       = urlPriorityService.getPriority(url, configuration.urlPriorities)
      val message        = AddUrlToQueue(urlNode, priority)
      delay match {
        case None =>
          frontierPriorityQueue ! message
        case Some(delayTime) =>
          context.system.scheduler.scheduleOnce(delayTime, frontierPriorityQueue, message)
      }
      context.become(checkingUrls(updatedJournal, queuedUrls + urlNode, children, websitesData, responseTo))

    case ScrapFromUrl(url, depth, scenario) =>
      val urlNode = UrlNode(url, depth, scenario)
      val worker  = scraperResolverService.getScraper(url, depth, scenario, configuration, context)
      context.become(
        checkingUrls(
          scheduledVisitJournal,
          queuedUrls - urlNode,
          children + worker,
          websitesData + WebsiteData(url, scenario.map(_.name), Map.empty),
          responseTo
        )
      )

    case CheckDone(websiteData) =>
      onCheckDone(
        scheduledVisitJournal,
        queuedUrls,
        children,
        websitesData.map(
          el =>
            if (el.url == websiteData.url && el.scrapedWithScenario == websiteData.scrapedWithScenario) websiteData
            else el
        ),
        websiteData,
        responseTo
      )
  }

  private def resolveIfAlreadyScraped(
    websitesData: Set[WebsiteData],
    url: String,
    scenario: Option[ScrapingScenario]
  ) = {
    val maybeScenarioName = scenario.map(_.name)
    !websitesData.exists(data => data.url == url && data.scrapedWithScenario == maybeScenarioName)
  }
}

object BaseController {

  case class StartScraping(url: String)

  case class ScrapFromUrl(
    url: String,
    depth: Int,
    scenario: Option[ScrapingScenario]
  )

  case class CheckDone(
    websiteData: WebsiteData
  )

  case class AddUrl(
    url: String,
    depth: Int,
    scenario: Option[ScrapingScenario]
  )
}
