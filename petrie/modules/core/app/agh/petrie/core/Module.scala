package agh.petrie.core

import javax.inject.{Inject, Provider, Singleton}

import agh.petrie.scraping.WebScraper
import akka.actor.ActorSystem
import com.google.inject.AbstractModule
import play.api.{Configuration, Environment}


class Module(environment: Environment, configuration: Configuration) extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[WebScraper]).toProvider(classOf[WebScraperProvider])
  }
}

@Singleton
class WebScraperProvider @Inject() (actorSystem: ActorSystem) extends Provider[WebScraper] {
  lazy val get = WebScraper(actorSystem)
}



