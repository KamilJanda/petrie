package agh.petrie.scraping.actors

import agh.petrie.scraping.actors.Receptionist.{FetchedUrls, GetUrls, GetUrlsAsync, Job}
import agh.petrie.scraping.api.BasicScrapingApi
import agh.petrie.scraping.service.HtmlParsingService
import agh.petrie.scraping.web.AsyncScrapingService
import akka.actor.{Actor, ActorRef, Props}

class Receptionist(asyncScrapingService: AsyncScrapingService, htmlParsingService: HtmlParsingService) extends Actor {

  override def receive: Receive = idle

  def idle: Receive = {
    case GetUrls(url, depth) => context.become(runNextJob(Vector(Job(sender, Controller.CheckUrl(url, depth)))))
    case GetUrlsAsync(url, depth, ref) => fetchAsync(url, depth, ref)
  }

  def working(jobs: Vector[Job]): Receive = {
    case message: FetchedUrls =>
      val job = jobs.head
      job.client !  message
      runNextJob(jobs.tail)
    case GetUrls(url, depth) => context.become(working(jobs :+ Job(sender, Controller.CheckUrl(url, depth))))
    case GetUrlsAsync(url, depth, ref) => fetchAsync(url, depth, ref)
  }

  private def runNextJob(jobs: Vector[Job]): Receive = {
    if (jobs.isEmpty) {
      idle
    } else {
      val job = jobs.head
      val controller = context.actorOf(Controller.props(asyncScrapingService, htmlParsingService))
      controller ! job.action
      working(jobs)
    }
  }

  private def fetchAsync(
    rootUrl:   String,
    depth:     Int,
    streamRef: akka.actor.typed.ActorRef[BasicScrapingApi.Protocol]
  ) = {
    val controller = context.actorOf(Controller.props(asyncScrapingService, htmlParsingService))
    controller ! GetUrlsAsync(rootUrl, depth, streamRef)
  }
}

object Receptionist {
  def props(asyncScrapingService: AsyncScrapingService, htmlParsingService: HtmlParsingService) =
    Props(new Receptionist(asyncScrapingService, htmlParsingService))

  case class GetUrls(rootUrl: String, depth: Int)
  case class GetUrlsAsync(rootUrl: String, depth: Int, streamRef: akka.actor.typed.ActorRef[BasicScrapingApi.Protocol])
  case class FetchedUrls(urls: Set[String])

  private case class Job(client: ActorRef, action: Controller.CheckUrl)
}
