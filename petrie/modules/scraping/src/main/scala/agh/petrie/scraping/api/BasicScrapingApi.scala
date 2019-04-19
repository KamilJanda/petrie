package agh.petrie.scraping.api

import agh.petrie.scraping.actors.Receptionist.{FetchedUrls, GetUrls}
import akka.actor.{ActorRef, Props}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.Future

trait BasicScrapingApi { self: TopLevelActorsDefined =>


  def getAllLinks(rootUrl: String, depth: Int)(implicit t: Timeout): Future[FetchedUrls] = {
    (receptionist ? GetUrls(rootUrl, depth)).asInstanceOf[Future[FetchedUrls]]
  }

  def fetchLinksAsync(
    socket: ActorRef
  ): Props = {
    getAsyncReceptionist(socket)
  }

}

object BasicScrapingApi {

  sealed trait Protocol
  case class Message(msg: String) extends Protocol
  case object Complete extends Protocol {
    val message: String = "done"
  }
}
