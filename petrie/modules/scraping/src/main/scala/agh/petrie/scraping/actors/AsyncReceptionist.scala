package agh.petrie.scraping.actors

import agh.petrie.scraping.actors.AsyncReceptionist.GetUrlsAsync
import agh.petrie.scraping.actors.Controller.CheckUrlAsync
import agh.petrie.scraping.model.Configuration
import agh.petrie.scraping.service.HtmlParsingService
import agh.petrie.scraping.web.AsyncScrapingService
import akka.actor.{Actor, ActorRef, Props}

class AsyncReceptionist(asyncScrapingService: AsyncScrapingService, htmlParsingService: HtmlParsingService, socket: ActorRef) extends Actor {

  override def receive: Receive = {
    case GetUrlsAsync(url, depth, configuration) =>
      fetchAsync(url, depth, configuration)
  }

  private def fetchAsync(
    rootUrl:   String,
    depth:     Int,
    configuration: Configuration
  ) = {
    val controller = context.actorOf(Controller.props(asyncScrapingService, htmlParsingService))
    controller ! CheckUrlAsync(rootUrl, depth, socket, configuration)
  }
}

object AsyncReceptionist {
  def props(asyncScrapingService: AsyncScrapingService, htmlParsingService: HtmlParsingService)(socket: ActorRef) =
    Props(new AsyncReceptionist(asyncScrapingService, htmlParsingService, socket))

  case class GetUrlsAsync(rootUrl: String, depth: Int, configuration: Configuration)
}

