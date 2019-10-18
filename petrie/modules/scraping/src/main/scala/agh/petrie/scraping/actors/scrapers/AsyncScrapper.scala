package agh.petrie.scraping.actors.scrapers

import agh.petrie.scraping.actors.controllers.BaseController.{CheckDone, ScrapFromUrl}
import agh.petrie.scraping.actors.receptionist.SimpleReceptionist.WebsiteData
import agh.petrie.scraping.actors.scrapers.AsyncScrapper.Stop
import agh.petrie.scraping.model.{Configuration, ScrapingScenario}
import agh.petrie.scraping.service.HtmlParsingService
import agh.petrie.scraping.service.HtmlParsingService.Html
import agh.petrie.scraping.web.AsyncScrapingService
import akka.actor.{Actor, Props}
import akka.pattern.pipe

import scala.concurrent.duration._

class AsyncScrapper(
  url: String,
  depth: Int,
  configuration: Configuration,
  scrapingScenario: Option[ScrapingScenario],
  asyncScrapingService: AsyncScrapingService,
  htmlParsingService: HtmlParsingService,
  timeout: Int
) extends Actor {

  implicit val exec = context.dispatcher

  asyncScrapingService.getUrlContent(url).pipeTo(self)
  context.system.scheduler.scheduleOnce(timeout second, self, Stop)

  override def receive: Receive = {
    case html: Html =>
      val nextScenario = scrapingScenario.flatMap(_.targetScenario)
      val fetchResult =
        htmlParsingService.fetchContent(html, scrapingScenario.toRight(configuration.noScenarioFallback))
      fetchResult.urls.foreach { url =>
        context.parent ! ScrapFromUrl(url, depth - 1, nextScenario)
      }
      context.parent ! CheckDone(WebsiteData(url, fetchResult.text))
      context.stop(self)
    case Stop =>
      context.parent ! CheckDone(WebsiteData(url, None))
      context.stop(self)
  }
}

object AsyncScrapper {
  def props(
    url: String,
    depth: Int,
    configuration: Configuration,
    scrapingScenario: Option[ScrapingScenario],
    asyncScrapingService: AsyncScrapingService,
    htmlParsingService: HtmlParsingService,
    timeout: Int
  ) =
    Props(
      new AsyncScrapper(url, depth, configuration, scrapingScenario, asyncScrapingService, htmlParsingService, timeout)
    )

  case object Stop
}
