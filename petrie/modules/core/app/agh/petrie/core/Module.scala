package agh.petrie.core

import agh.petrie.scraping.{WebScraper, WebScraperConfiguration}
import akka.actor.ActorSystem
import com.google.inject.AbstractModule
import javax.inject.{Inject, Provider, Singleton}
import play.api.{Configuration, Environment}

class Module(environment: Environment, configuration: Configuration) extends AbstractModule {
  override def configure(): Unit =
    bind(classOf[WebScraper]).toProvider(classOf[WebScraperProvider])
}

@Singleton
class WebScraperProvider @Inject()(actorSystem: ActorSystem, configuration: Configuration)
  extends Provider[WebScraper] {
  lazy val get = WebScraper(
    actorSystem,
    WebScraperConfiguration(
      configuration.underlying.getInt("webscraper.selenium.drivers.count"),
      configuration.underlying.getInt("webscraper.scraper.async.timeout"),
      configuration.underlying.getInt("webscraper.scraper.dynamic.timeout"),
      configuration.underlying.getInt("webscraper.scraper.throttling.delay")
    )
  )
}
