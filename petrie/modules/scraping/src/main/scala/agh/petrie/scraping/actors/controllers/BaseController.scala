package agh.petrie.scraping.actors.controllers

import agh.petrie.scraping.actors.controllers.BaseController.{CheckDone, ScrapFromUrl, StartScraping}
import agh.petrie.scraping.model.{Configuration, FallbackScenario, ScrapingScenario}
import agh.petrie.scraping.service.ScraperResolverService
import akka.actor.{Actor, ActorRef}

abstract class BaseController(
  scraperResolverService: ScraperResolverService,
  configuration:          Configuration
) extends Actor {

  def onNegativeDepth(): Unit
  def onFetchedUrl(url : String, responseTo: ActorRef): Unit
  def onCheckDone(children: Set[ActorRef], urls: Set[String], responseTo: ActorRef): Unit

  override def receive: Receive = {
    case StartScraping(url) if configuration.maxSearchDepth >= 0 =>
      context.become(checkingUrls(Set.empty, Set.empty, sender))
      configuration.rootScenario.foreach(scenario => {
        self ! ScrapFromUrl(url, configuration.maxSearchDepth, Some(scenario))
      })

    case _: StartScraping => onNegativeDepth()
  }

  def checkingUrls(children: Set[ActorRef], urls: Set[String], responseTo: ActorRef): Receive = {
    case ScrapFromUrl(url , depth, scenario) if depth >= 0 && !urls.contains(url) =>
      val worker = scraperResolverService.getScraper(url, depth, scenario, configuration, context)
      context.become(checkingUrls(children + worker, urls + url, responseTo))
      onFetchedUrl(url, responseTo)

    case ScrapFromUrl(url, _, _) =>
      onFetchedUrl(url ,responseTo)
      context.become(checkingUrls(children, urls + url, responseTo))

    case CheckDone =>
      onCheckDone(children, urls, responseTo)
  }
}

object BaseController {

  case class ScrapFromUrl(
    url: String,
    depth: Int,
    scenario: Option[ScrapingScenario]
  )
  case class StartScraping(url: String)
  case object CheckDone
}
