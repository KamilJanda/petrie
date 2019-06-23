package agh.petrie.scraping.actors.receptionist

import agh.petrie.scraping.actors.controllers.Controller
import agh.petrie.scraping.actors.receptionist.Receptionist.{FetchedUrls, GetUrls, Job}
import agh.petrie.scraping.model.Configuration
import agh.petrie.scraping.service.GetterResolverService
import akka.actor.{Actor, ActorRef, Props}

class Receptionist(
  getterResolverService: GetterResolverService
) extends Actor {

  override def receive: Receive = idle

  def idle: Receive = {
    case GetUrls(url, depth, configuration) =>
      context.become(runNextJob(Vector(Job(sender, Controller.CheckUrl(url, depth, configuration)))))
  }

  def working(jobs: Vector[Job]): Receive = {
    case message: FetchedUrls =>
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
      val controller = context.actorOf(Controller.props(getterResolverService))
      controller ! job.action
      working(jobs)
    }
  }

}

object Receptionist {
  def props(getterResolverService: GetterResolverService) =
    Props(new Receptionist(getterResolverService))

  case class GetUrls(rootUrl: String, depth: Int, configuration: Configuration)
  case class FetchedUrls(urls: Set[String])

  private case class Job(client: ActorRef, action: Controller.CheckUrl)
}
