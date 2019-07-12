package agh.petrie.scraping

import agh.petrie.scraping.actors.receptionist.{Receptionist, StreamingReceptionist}
import agh.petrie.scraping.api.{BasicScrapingApi, TopLevelActorsDefined}
import akka.actor.{ActorRef, ActorSystem}

case class WebScraper(actorSystem: ActorSystem, webScraperConfiguration: WebScraperConfiguration) extends ScrapingModule with BasicScrapingApi with TopLevelActorsDefined {

  private[scraping] override val receptionist: ActorRef = actorSystem.actorOf(Receptionist.props(getterResolverService))

  private[scraping] def getAsyncReceptionist(socket: ActorRef) = StreamingReceptionist.props(getterResolverService)(socket)

  def close = {
    actorSystem.stop(receptionist)
    asyncHttpClient.close()
  }
}
