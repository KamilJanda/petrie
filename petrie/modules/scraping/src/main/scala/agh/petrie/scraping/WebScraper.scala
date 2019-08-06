package agh.petrie.scraping

import agh.petrie.scraping.actors.receptionist.{SimpleReceptionist, StreamingReceptionist}
import agh.petrie.scraping.api.{BasicScrapingApi, TopLevelActorsDefined}
import akka.actor.{ActorRef, ActorSystem}

case class WebScraper(actorSystem: ActorSystem, webScraperConfiguration: WebScraperConfiguration) extends ScrapingModule with BasicScrapingApi with TopLevelActorsDefined {

  private[scraping] override val receptionist: ActorRef = actorSystem.actorOf(SimpleReceptionist.props(scraperResolverService))

  private[scraping] def getAsyncReceptionist(socket: ActorRef) = StreamingReceptionist.props(scraperResolverService)(socket)

  def close = {
    actorSystem.stop(receptionist)
    asyncHttpClient.close()
  }
}
