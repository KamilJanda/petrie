package agh.petrie.scraping.actors.scrapers

import agh.petrie.scraping.actors.controllers.BaseController.{CheckDone, ScrapFromUrl}
import agh.petrie.scraping.actors.receptionist.SimpleReceptionist.WebsiteData
import agh.petrie.scraping.actors.scrapers.AsyncScrapper.Stop
import agh.petrie.scraping.actors.scrapers.DynamicScrapper.WorkStarted
import agh.petrie.scraping.actors.scrapers.SeleniumWorker.FetchFromUrl
import agh.petrie.scraping.model.{Configuration, ScrapingScenario}
import agh.petrie.scraping.service.HtmlParsingService
import agh.petrie.scraping.service.HtmlParsingService.Html
import akka.actor.{Actor, ActorRef, Props}

import scala.concurrent.duration._

class DynamicScrapper(
  url: String,
  depth: Int,
  configuration: Configuration,
  scrapingScenario: Option[ScrapingScenario],
  htmlParsingService: HtmlParsingService,
  workerRouter: ActorRef,
  timeout: Int
) extends Actor {

  implicit val exec = context.dispatcher

  workerRouter ! FetchFromUrl(url, scrapingScenario.map(_.preScrapingConfiguration))

  override def receive: Receive = {
    case html: Html =>
      val nextScenario = scrapingScenario.flatMap(_.targetScenario)
      val fetchResult =
        htmlParsingService.fetchContent(html, scrapingScenario.toRight(configuration.noScenarioFallback))
      fetchResult.urls.foreach { url =>
        context.parent ! ScrapFromUrl(url, depth - 1, nextScenario)
      }
      context.parent !  CheckDone(WebsiteData(url, fetchResult.text))
      context.stop(self)
    case WorkStarted =>
      context.system.scheduler.scheduleOnce(timeout second, self, Stop)
    case Stop =>
      context.parent ! CheckDone(WebsiteData(url, None))
      context.stop(self)
  }
}

object DynamicScrapper {

  def props(
    url: String,
    depth: Int,
    configuration: Configuration,
    scrapingScenario: Option[ScrapingScenario],
    workerRouter: ActorRef,
    htmlParsingService: HtmlParsingService,
    timeout: Int
  ) = Props(new DynamicScrapper(url, depth, configuration, scrapingScenario, htmlParsingService, workerRouter, timeout))

  case object Stop
  case object WorkStarted
}
