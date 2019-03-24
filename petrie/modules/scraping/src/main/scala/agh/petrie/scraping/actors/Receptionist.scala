package agh.petrie.scraping.actors

import akka.actor.{Actor, ActorRef, Props}
import agh.petrie.scraping.actors.Controller.CheckUrl
import agh.petrie.scraping.actors.Receptionist.{GetUrls, Job, FetchedUrls}
import agh.petrie.scraping.service.HtmlParsingService
import agh.petrie.scraping.web.AsyncScrapingService

class Receptionist(asyncScrapingService: AsyncScrapingService, htmlParsingService: HtmlParsingService) extends Actor {

  override def receive: Receive = idle

  def idle: Receive = {
    case GetUrls(url, depth) => context.become(runNextJob(Vector(Job(sender, Controller.CheckUrl(url, depth)))))
  }

  def working(jobs: Vector[Job]): Receive = {
    case message: FetchedUrls =>
      val job = jobs.head
      job.client !  message
      runNextJob(jobs.tail)

    case GetUrls(url, depth) => context.become(working(jobs :+ Job(sender, Controller.CheckUrl(url, depth))))
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
}

object Receptionist {
  def props(asyncScrapingService: AsyncScrapingService, htmlParsingService: HtmlParsingService) =
    Props(new Receptionist(asyncScrapingService, htmlParsingService))

  case class GetUrls(rootUrl: String, depth: Int)
  case class FetchedUrls(urls: Set[String])

  private case class Job(client: ActorRef, action: Controller.CheckUrl)
}
