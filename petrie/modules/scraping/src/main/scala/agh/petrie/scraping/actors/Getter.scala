package agh.petrie.scraping.actors

import akka.actor.{Actor, Props}
import akka.pattern.pipe
import agh.petrie.scraping.actors.Controller.CheckUrl
import agh.petrie.scraping.service.HtmlParsingService
import agh.petrie.scraping.web.AsyncScrapingService
import agh.petrie.scraping.web.AsyncScrapingService.Html

class Getter(
  url:                  String,
  depth:                Int,
  asyncScrapingService: AsyncScrapingService,
  htmlParsingService:   HtmlParsingService
) extends Actor {

  implicit val exec = context.dispatcher

  asyncScrapingService.getUrlContent(url) pipeTo self

  override def receive: Receive = {
    case html: Html =>
      htmlParsingService.fetchUrls(html).foreach{ url =>
        context.parent ! CheckUrl(url, depth - 1)
      }
      context.parent ! Controller.CheckDone
      context.stop(self)
  }
}

object Getter {
  def props(
    url: String,
    depth: Int,
    asyncScrapingService: AsyncScrapingService,
    htmlParsingService: HtmlParsingService
  ) = Props(new Getter(url, depth, asyncScrapingService, htmlParsingService))

}