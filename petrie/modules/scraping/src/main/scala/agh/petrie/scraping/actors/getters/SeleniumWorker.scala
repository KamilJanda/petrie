package agh.petrie.scraping.actors.getters

import agh.petrie.scraping.actors.getters.SeleniumWorker.FetchFromUrl
import agh.petrie.scraping.model.Configuration
import agh.petrie.scraping.web.SeleniumScrapingService
import akka.actor.{Actor, Props}
import akka.pattern.pipe

class SeleniumWorker(seleniumScrapingService: SeleniumScrapingService) extends Actor {

  implicit val exec = context.dispatcher

  override def receive: Receive = {
    case FetchFromUrl(url, configuration) =>
      seleniumScrapingService.getUrlContent(url, configuration).pipeTo(sender())
  }
}

object SeleniumWorker {
  trait SeleniumWorkerMessage
  case class FetchFromUrl(url: String, configuration: Configuration) extends SeleniumWorkerMessage

  def props(seleniumScrapingService: SeleniumScrapingService) = Props(new SeleniumWorker(seleniumScrapingService))
}