package agh.petrie.core.viewconverters

import agh.petrie.core.model.view.ConfigurationView
import agh.petrie.core.viewconverters.WebsocketConverterActor.GetUrlsView
import agh.petrie.scraping.actors.receptionist.StreamingReceptionist.GetUrls
import agh.petrie.scraping.actors.scrapers.SeleniumWorker
import akka.actor.{Actor, ActorRef, Props}

class WebsocketConverterActor(receptionist: ActorRef, configurationViewConverter: ConfigurationViewConverter) extends Actor {
  override def receive: Receive = {
    case GetUrlsView(url, config) =>
      receptionist forward GetUrls(url, configurationViewConverter.fromView(config))
  }
}

object WebsocketConverterActor{
  case class GetUrlsView(rootUrl: String, configuration: ConfigurationView)

  def props(receptionist: ActorRef, configurationViewConverter: ConfigurationViewConverter) =  Props(new WebsocketConverterActor(receptionist, configurationViewConverter))
}