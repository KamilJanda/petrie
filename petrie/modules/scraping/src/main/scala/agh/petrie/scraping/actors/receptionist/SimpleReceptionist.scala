package agh.petrie.scraping.actors.receptionist

import agh.petrie.scraping.actors.controllers.BaseController.StartScraping
import agh.petrie.scraping.actors.controllers.SimpleController
import agh.petrie.scraping.actors.receptionist.SimpleReceptionist.{FetchedData, GetUrls, Job}
import agh.petrie.scraping.model.Configuration
import agh.petrie.scraping.service.{ScraperResolverService, ThrottlingService, UrlPriorityService}
import akka.actor.{Actor, ActorRef, Props}

class SimpleReceptionist(
  scraperResolverService: ScraperResolverService,
  throttlingService: ThrottlingService,
  urlPriorityService: UrlPriorityService
) extends Actor {

  override def receive: Receive = idle

  def idle: Receive = {
    case GetUrls(url, configuration) =>
      context.become(runNextJob(Vector(Job(sender, configuration, StartScraping(url)))))
  }

  def working(jobs: Vector[Job]): Receive = {
    case message: FetchedData =>
      val job = jobs.head
      job.client ! message
      context.become(runNextJob(jobs.tail))
    case GetUrls(url, configuration) =>
      context.become(working(jobs :+ Job(sender, configuration, StartScraping(url))))
  }

  def runNextJob(jobs: Vector[Job]): Receive = {
    if (jobs.isEmpty) {
      idle
    } else {
      val job = jobs.head
      val controller =
        context.actorOf(
          SimpleController.props(scraperResolverService, throttlingService, urlPriorityService, job.configuration)
        )
      controller ! job.action
      working(jobs)
    }
  }

}

object SimpleReceptionist {
  def props(
    scraperResolverService: ScraperResolverService,
    throttlingService: ThrottlingService,
    urlPriorityService: UrlPriorityService
  ) =
    Props(new SimpleReceptionist(scraperResolverService, throttlingService, urlPriorityService))

  case class GetUrls(rootUrl: String, configuration: Configuration)
  case class FetchedData(
    result: Set[WebsiteData]
  )

  case class WebsiteData(
    url: String,
    scrapedWithScenario: Option[String],
    content: Map[String, String]
  )

  case class Job(client: ActorRef, configuration: Configuration, action: StartScraping)
}
