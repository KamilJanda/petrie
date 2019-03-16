package scraping.actors

import akka.actor.{Actor, Props}
import akka.pattern.pipe
import scraping.actors.Controller.CheckUrl
import scraping.service.HtmlParsingService
import scraping.web.AsyncScrapingService
import scraping.web.AsyncScrapingService.Html

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