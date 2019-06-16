package agh.petrie.scraping.actors

import agh.petrie.scraping.actors.Receptionist.{FetchedUrls, GetUrls, Job}
import agh.petrie.scraping.model.Configuration
import agh.petrie.scraping.service.HtmlParsingService
import agh.petrie.scraping.web.AsyncScrapingService
import akka.actor.{Actor, ActorRef, Props}

class Receptionist(asyncScrapingService: AsyncScrapingService, htmlParsingService: HtmlParsingService) extends Actor {

  override def receive: Receive = idle

  def idle: Receive = {
    case GetUrls(url, depth, configuration) =>
      context.become(runNextJob(Vector(Job(sender, Controller.CheckUrl(url, depth, configuration)))))
  }

  def working(jobs: Vector[Job]): Receive = {
    case message: FetchedUrls =>
      println(message)
      val job = jobs.head
      job.client !  message
      context.become(runNextJob(jobs.tail))
    case GetUrls(url, depth, configuration) =>
      context.become(working(jobs :+ Job(sender, Controller.CheckUrl(url, depth, configuration))))
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

  case class GetUrls(rootUrl: String, depth: Int, configuration: Configuration)
  case class FetchedUrls(urls: Set[String])

  private case class Job(client: ActorRef, action: Controller.CheckUrl)
}
