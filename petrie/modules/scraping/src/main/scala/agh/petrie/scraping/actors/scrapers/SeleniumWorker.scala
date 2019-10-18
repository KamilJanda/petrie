package agh.petrie.scraping.actors.scrapers

import agh.petrie.scraping.actors.scrapers.DynamicScrapper.WorkStarted
import agh.petrie.scraping.actors.scrapers.SeleniumWorker.FetchFromUrl
import agh.petrie.scraping.model.{Configuration, PreScrapingConfiguration}
import agh.petrie.scraping.web.SeleniumScrapingService
import akka.actor.{Actor, Props}
import akka.pattern.pipe

class SeleniumWorker(seleniumScrapingService: SeleniumScrapingService) extends Actor {

  implicit val exec = context.dispatcher

  override def receive: Receive = {
    case FetchFromUrl(url, configuration) =>
      sender ! WorkStarted
      seleniumScrapingService.getUrlContent(url, configuration).pipeTo(sender())
  }
}

object SeleniumWorker {
  trait SeleniumWorkerMessage
  case class FetchFromUrl(url: String, preScrapingConfiguration: Option[PreScrapingConfiguration])
    extends SeleniumWorkerMessage

  def props(seleniumScrapingService: SeleniumScrapingService) = Props(new SeleniumWorker(seleniumScrapingService))
}
