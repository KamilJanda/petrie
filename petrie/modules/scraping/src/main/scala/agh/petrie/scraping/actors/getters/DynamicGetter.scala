package agh.petrie.scraping.actors.getters

import agh.petrie.scraping.actors.controllers.Controller
import agh.petrie.scraping.actors.controllers.Controller.CheckUrl
import agh.petrie.scraping.actors.getters.DynamicGetter.{Stop, WorkStarted}
import agh.petrie.scraping.actors.getters.SeleniumWorker.FetchFromUrl
import agh.petrie.scraping.model.Configuration
import agh.petrie.scraping.service.HtmlParsingService
import agh.petrie.scraping.service.HtmlParsingService.Html
import akka.actor.{Actor, ActorRef, Props}

import scala.concurrent.duration._

class DynamicGetter(
  url:                     String,
  depth:                   Int,
  configuration:           Configuration,
  htmlParsingService:      HtmlParsingService,
  workerRouter:            ActorRef,
  timeout:                 Int
) extends Actor {

  implicit val exec = context.dispatcher

  workerRouter ! FetchFromUrl(url, configuration)

  override def receive: Receive = {
    case html: Html =>
      htmlParsingService.fetchUrls(html, configuration).foreach { url =>
        context.parent ! CheckUrl(url, depth - 1, configuration)
      }
      context.parent ! Controller.CheckDone
      context.stop(self)
    case WorkStarted =>
      context.system.scheduler.scheduleOnce(timeout second, self, Stop)
    case Stop =>
      context.parent ! Controller.CheckDone
      context.stop(self)
  }
}


object DynamicGetter {
  def props(
    url: String,
    depth: Int,
    configuration: Configuration,
    workerRouter: ActorRef,
    htmlParsingService: HtmlParsingService,
    timeout: Int
  ) = Props(new DynamicGetter(url, depth, configuration, htmlParsingService, workerRouter, timeout))

  case object Stop
  case object WorkStarted
}