package scraping.actors

import akka.actor.{Actor, Props}
import scraping.web.AsyncScrapingService
import scala.concurrent.ExecutionContext.Implicits.global
import akka.pattern.pipe

class WebScraper(url: String, asyncScrapingService: AsyncScrapingService) extends Actor {

  implicit val exec = context.dispatcher

  asyncScrapingService.getUrlContent(url) pipeTo self

  override def receive: Receive = {
    case body: String => println(body);
  }
}

object WebScraper {
  def props(url: String, asyncScrapingService: AsyncScrapingService) = Props(new WebScraper(url, asyncScrapingService))
}