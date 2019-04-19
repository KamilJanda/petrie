package agh.petrie.scraping

import agh.petrie.scraping.actors.{AsyncReceptionist, Receptionist}
import agh.petrie.scraping.api.{BasicScrapingApi, TopLevelActorsDefined}
import akka.actor.{ActorRef, ActorSystem}

case class WebScraper(actorSystem: ActorSystem) extends ScrapingModule with BasicScrapingApi with TopLevelActorsDefined {

  private[scraping] override val receptionist: ActorRef = actorSystem.actorOf(Receptionist.props(asyncScrapingService, htmlParsingService))

  private[scraping] def getAsyncReceptionist(socket: ActorRef) = AsyncReceptionist.props(asyncScrapingService, htmlParsingService)(socket)

  def close = {
    actorSystem.stop(receptionist)
    asyncHttpClient.close()
  }
}
