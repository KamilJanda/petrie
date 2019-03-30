package agh.petrie.scraping.api

import akka.actor.ActorRef

trait TopLevelActorsDefined {

  private[scraping] def receptionst: ActorRef
}
