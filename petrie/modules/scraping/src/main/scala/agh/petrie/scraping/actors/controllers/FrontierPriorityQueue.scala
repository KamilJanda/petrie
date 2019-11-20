package agh.petrie.scraping.actors.controllers

import agh.petrie.scraping.actors.controllers.BaseController.ScrapFromUrl
import agh.petrie.scraping.actors.controllers.FrontierPriorityQueue.{AddUrlToQueue, HighPriority, LowPriority, UrlNode}
import agh.petrie.scraping.model.ScrapingScenario
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.dispatch.{PriorityGenerator, UnboundedStablePriorityMailbox}
import com.typesafe.config.Config

class FrontierPriorityQueue(controller: ActorRef) extends Actor {
  override def receive: Receive = {
    case AddUrlToQueue(UrlNode(url, depth, scenario), _) =>
      controller ! ScrapFromUrl(url, depth, scenario)
  }
}

object FrontierPriorityQueue {
  def props(controller: ActorRef) =
    Props(new FrontierPriorityQueue(controller))

  sealed trait Priority

  case object HighPriority extends Priority
  case object LowPriority  extends Priority

  case class AddUrlToQueue(urlNode: UrlNode, priority: Priority)

  case class UrlNode(
    url: String,
    depth: Int,
    scenario: Option[ScrapingScenario]
  )
}

class FrontierPriorityQueueMailbox(settings: ActorSystem.Settings, config: Config)
  extends UnboundedStablePriorityMailbox(PriorityGenerator {
    case AddUrlToQueue(_, HighPriority) => 0
    case AddUrlToQueue(_, LowPriority)  => 3
  })
