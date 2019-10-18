package agh.petrie.scraping.service

import agh.petrie.scraping.WebScraperConfiguration
import agh.petrie.scraping.actors.scrapers.{AsyncScrapper, DynamicScrapper, SeleniumWorkerRouter}
import agh.petrie.scraping.model.{Configuration, DynamicScraping, ScrapingScenario}
import agh.petrie.scraping.web.AsyncScrapingService
import akka.actor.{ActorContext, ActorSystem}

class ScraperResolverService(
  asyncScrapingService: AsyncScrapingService,
  htmlParsingService: HtmlParsingService,
  webScraperConfiguration: WebScraperConfiguration,
  actorSystem: ActorSystem
) {

  val seleniumWorkerRouter = actorSystem.actorOf(SeleniumWorkerRouter.props(webScraperConfiguration))

  def getScraper(
    url: String,
    depth: Int,
    scrapingScenario: Option[ScrapingScenario],
    configuration: Configuration,
    context: ActorContext
  ) = {
    if (configuration.scrapingType == DynamicScraping) {
      context.actorOf(
        DynamicScrapper.props(
          url,
          depth,
          configuration,
          scrapingScenario,
          seleniumWorkerRouter,
          htmlParsingService,
          webScraperConfiguration.dynamicScraperTimeoutInSeconds
        )
      )
    } else {
      context.actorOf(
        AsyncScrapper.props(
          url,
          depth,
          configuration,
          scrapingScenario,
          asyncScrapingService,
          htmlParsingService,
          webScraperConfiguration.asyncScraperTimeoutInSeconds
        )
      )
    }
  }
}
