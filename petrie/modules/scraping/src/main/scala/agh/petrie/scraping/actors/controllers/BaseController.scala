package agh.petrie.scraping.actors.controllers

import agh.petrie.scraping.actors.controllers.BaseController.{CheckDone, ScrapFromUrl, StartScraping}
import agh.petrie.scraping.actors.receptionist.SimpleReceptionist.WebsiteData
import agh.petrie.scraping.model.{Configuration, FallbackScenario, ScrapingScenario}
import agh.petrie.scraping.service.ScraperResolverService
import akka.actor.{Actor, ActorRef}

abstract class BaseController(
  scraperResolverService: ScraperResolverService,
  configuration:          Configuration
) extends Actor {

  def onNegativeDepth(): Unit
  def onCheckDone(children: Set[ActorRef], websitesData: Set[WebsiteData], fetched: WebsiteData, responseTo: ActorRef): Unit

  override def receive: Receive = {
    case StartScraping(url) if configuration.maxSearchDepth >= 0 =>
      context.become(checkingUrls(Set.empty, Set.empty, sender))
      configuration.rootScenario.foreach(scenario => {
        self ! ScrapFromUrl(url, configuration.maxSearchDepth, Some(scenario))
      })

    case _: StartScraping => onNegativeDepth()
  }

  def checkingUrls(children: Set[ActorRef], websitesData: Set[WebsiteData], responseTo: ActorRef): Receive = {
    case ScrapFromUrl(url, depth, scenario) if depth >= 0 && !websitesData.map(_.url).contains(url) =>
      val worker = scraperResolverService.getScraper(url, depth, scenario, configuration, context)
      context.become(checkingUrls(children + worker, websitesData, responseTo))

    case ScrapFromUrl(url, _, _) =>
      context.become(checkingUrls(children, websitesData, responseTo))

    case CheckDone(websiteData) =>
      onCheckDone(children, websitesData + websiteData, websiteData, responseTo)
  }
}

object BaseController {

  case class ScrapFromUrl(
    url: String,
    depth: Int,
    scenario: Option[ScrapingScenario]
  )
  case class StartScraping(url: String)
  case class CheckDone(
    websiteData: WebsiteData
  )
}
