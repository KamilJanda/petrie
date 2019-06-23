package agh.petrie.scraping.actors.receptionist

import agh.petrie.scraping.actors.controllers.Controller
import agh.petrie.scraping.actors.controllers.Controller.CheckUrlAsync
import agh.petrie.scraping.actors.receptionist.StreamingReceptionist.GetUrlsAsync
import agh.petrie.scraping.model.Configuration
import agh.petrie.scraping.service.GetterResolverService
import akka.actor.{Actor, ActorRef, Props}

class StreamingReceptionist(
  getterResolverService:  GetterResolverService,
  socket:                 ActorRef
) extends Actor {

  override def receive: Receive = {
    case GetUrlsAsync(url, depth, configuration) =>
      stream(url, depth, configuration)
  }

  private def stream(
    rootUrl:   String,
    depth:     Int,
    configuration: Configuration
  ) = {
    val controller = context.actorOf(Controller.props(getterResolverService))
    controller ! CheckUrlAsync(rootUrl, depth, socket, configuration)
  }
}

object StreamingReceptionist {
  def props(getterResolverService:  GetterResolverService)(socket: ActorRef) =
    Props(new StreamingReceptionist(getterResolverService, socket))

  case class GetUrlsAsync(rootUrl: String, depth: Int, configuration: Configuration)
}

