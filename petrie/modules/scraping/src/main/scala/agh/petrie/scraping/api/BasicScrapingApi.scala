package agh.petrie.scraping.api

import agh.petrie.scraping.actors.receptionist.SimpleReceptionist.{FetchedData, GetUrls, WebsiteData}
import agh.petrie.scraping.model.Configuration
import akka.actor.{ActorRef, Props}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.Future

trait BasicScrapingApi { self: TopLevelActorsDefined =>

  def getAllLinks(rootUrl: String, configuration: Configuration)(implicit t: Timeout): Future[FetchedData] = {
    (receptionist ? GetUrls(rootUrl, configuration)).asInstanceOf[Future[FetchedData]]
  }

  def fetchLinksAsync(
    socket: ActorRef
  ): Props = {
    getAsyncReceptionist(socket)
  }

}

object BasicScrapingApi {

  sealed trait Protocol
  case class Message(websiteData: WebsiteData) extends Protocol
  case object Complete extends Protocol {
    val message: String = "done"
  }
}
