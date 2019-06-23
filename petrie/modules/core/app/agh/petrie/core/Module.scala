package agh.petrie.core

import javax.inject.{Inject, Provider, Singleton}
import agh.petrie.scraping.{WebScraper, WebScraperConfiguration}
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
  // TODO: create type safe configuration for webscraper config and  hardcoded values like that
  lazy val get = WebScraper(actorSystem, WebScraperConfiguration(2))
}



