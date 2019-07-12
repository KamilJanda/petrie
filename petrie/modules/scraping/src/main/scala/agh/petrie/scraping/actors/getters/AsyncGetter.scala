package agh.petrie.scraping.actors.getters

import agh.petrie.scraping.actors.controllers.Controller
import agh.petrie.scraping.actors.controllers.Controller.CheckUrl
import agh.petrie.scraping.actors.getters.AsyncGetter.Stop
import agh.petrie.scraping.model.Configuration
import agh.petrie.scraping.service.HtmlParsingService
import agh.petrie.scraping.service.HtmlParsingService.Html
import agh.petrie.scraping.web.AsyncScrapingService
import akka.actor.{Actor, Props}
import akka.pattern.pipe

import scala.concurrent.duration._

class AsyncGetter(
  url:                  String,
  depth:                Int,
  configuration:        Configuration,
  asyncScrapingService: AsyncScrapingService,
  htmlParsingService:   HtmlParsingService,
  timeout:              Int
) extends Actor {

  implicit val exec = context.dispatcher

  asyncScrapingService.getUrlContent(url) pipeTo self
  context.system.scheduler.scheduleOnce(timeout second, self, Stop)

  override def receive: Receive = {
    case html: Html =>
      htmlParsingService.fetchUrls(html, configuration).foreach{ url =>
        context.parent ! CheckUrl(url, depth - 1, configuration)
      }
      context.parent ! Controller.CheckDone
      context.stop(self)
    case Stop =>
      context.parent ! Controller.CheckDone
      context.stop(self)
  }
}

object AsyncGetter {
  def props(
    url: String,
    depth: Int,
    configuration: Configuration,
    asyncScrapingService: AsyncScrapingService,
    htmlParsingService: HtmlParsingService,
    timeout: Int
  ) = Props(new AsyncGetter(url, depth, configuration, asyncScrapingService, htmlParsingService, timeout))

  case object Stop
}