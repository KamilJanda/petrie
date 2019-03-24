package agh.petrie.scraping.api

import agh.petrie.scraping.actors.Receptionist.{GetUrls, FetchedUrls}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.Future

trait BasicScrapingApi { self: TopLevelActorsDefined =>

  def getAllLinks(rootUrl: String, depth: Int)(implicit t: Timeout): Future[FetchedUrls] = {
    (receptionst ? GetUrls(rootUrl, depth)).asInstanceOf[Future[FetchedUrls]]
  }
}
