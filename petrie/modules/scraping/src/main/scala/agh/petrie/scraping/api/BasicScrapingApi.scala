package agh.petrie.scraping.api

import agh.petrie.scraping.WebScraper
import agh.petrie.scraping.actors.Receptionist.{FetchedUrls, GetUrls, GetUrlsAsync}
import akka.actor.typed.ActorRef
import akka.pattern.ask
import akka.stream.{Materializer, OverflowStrategy}
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.stream.typed.scaladsl.ActorSource
import akka.util.Timeout

import scala.concurrent.Future

trait BasicScrapingApi { self: TopLevelActorsDefined =>

  import BasicScrapingApi._

  def getAllLinks(rootUrl: String, depth: Int)(implicit t: Timeout): Future[FetchedUrls] = {
    (receptionst ? GetUrls(rootUrl, depth)).asInstanceOf[Future[FetchedUrls]]
  }

  def fetchLinksAsync(
    rootUrl: String,
    depth: Int
  )(implicit materializer: Materializer): Source[Protocol, ActorRef[Protocol]] = {
    val source: Source[Protocol, ActorRef[Protocol]] = ActorSource.actorRef[Protocol](
      completionMatcher = {
        case Complete ⇒
      },
      failureMatcher = {
        case Fail(ex) ⇒ throw new Exception(ex)
      },
      bufferSize = 8,
      overflowStrategy = OverflowStrategy.fail
    )

    val ref = Flow[Protocol]
      .to(Sink.ignore)
      .runWith(source)

    receptionst ! GetUrlsAsync(rootUrl, depth, ref)

    source
  }

}

object BasicScrapingApi {

  sealed trait Protocol
  case class Message(msg: String) extends Protocol
  case object Complete extends Protocol
  case class Fail(ex: String) extends Protocol
}
