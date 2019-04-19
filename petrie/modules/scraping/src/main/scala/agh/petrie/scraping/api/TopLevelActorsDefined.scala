package agh.petrie.scraping.api

import akka.actor.{ActorRef, Props}

trait TopLevelActorsDefined {

  private[scraping] def receptionist: ActorRef

  private[scraping] def getAsyncReceptionist(socket: ActorRef): Props
}
