package agh.petrie.scraping.service

import agh.petrie.scraping.WebScraperConfiguration
import agh.petrie.scraping.actors.getters.{AsyncGetter, DynamicGetter, SeleniumWorkerRouter}
import agh.petrie.scraping.model.Configuration
import agh.petrie.scraping.web.AsyncScrapingService
import akka.actor.{ActorContext, ActorSystem}

class GetterResolverService(
  asyncScrapingService:    AsyncScrapingService,
  htmlParsingService:      HtmlParsingService,
  webScraperConfiguration: WebScraperConfiguration,
  actorSystem:             ActorSystem
) {

  val seleniumWorkerRouter = actorSystem.actorOf(SeleniumWorkerRouter.props(webScraperConfiguration))

  def getGetter(
    url:                  String,
    depth:                Int,
    configuration:        Configuration,
    context:              ActorContext
  ) = {
    if (configuration.searchDynamically) {
      context.actorOf(DynamicGetter.props(url, depth, configuration, seleniumWorkerRouter, htmlParsingService))
    } else {
      println("dynamic")
      context.actorOf(AsyncGetter.props(url, depth, configuration,asyncScrapingService, htmlParsingService))
    }
  }
}
