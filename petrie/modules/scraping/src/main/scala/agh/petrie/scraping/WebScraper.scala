package agh.petrie.scraping

import agh.petrie.scraping.actors.Receptionist
import agh.petrie.scraping.api.{BasicScrapingApi, TopLevelActorsDefined}
import akka.actor.{ActorRef, ActorSystem}

case class WebScraper(actorSystem: ActorSystem) extends ScrapingModule with BasicScrapingApi with TopLevelActorsDefined {

  private[scraping] override val receptionst: ActorRef = actorSystem.actorOf(Receptionist.props(asyncScrapingService, htmlParsingService))

  def close = {
    actorSystem.stop(receptionst)
    asyncHttpClient.close()
  }
}
