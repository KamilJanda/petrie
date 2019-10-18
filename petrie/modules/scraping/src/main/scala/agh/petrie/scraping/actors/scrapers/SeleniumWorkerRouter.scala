package agh.petrie.scraping.actors.scrapers

import agh.petrie.scraping.WebScraperConfiguration
import agh.petrie.scraping.actors.scrapers.SeleniumWorker.SeleniumWorkerMessage
import agh.petrie.scraping.web.SeleniumScrapingService
import akka.actor.{Actor, Props, Terminated}
import akka.routing.{ActorRefRoutee, RoundRobinRoutingLogic, Router}

class SeleniumWorkerRouter(webScraperConfiguration: WebScraperConfiguration) extends Actor {

  var router = {
    val routees = (0 until webScraperConfiguration.seleniumDriversCount).map(id => {
      val seleniumScrapingService = new SeleniumScrapingService
      val routee                  = context.actorOf(SeleniumWorker.props(seleniumScrapingService), s"selenium_worker$id")
      context.watch(routee)
      ActorRefRoutee(routee)
    })
    Router(RoundRobinRoutingLogic(), routees)
  }

  def receive = {
    case message: SeleniumWorkerMessage =>
      router.route(message, sender())
    case Terminated(a) =>
      router = router.removeRoutee(a)
      val seleniumScrapingService = new SeleniumScrapingService
      val r                       = context.actorOf(SeleniumWorker.props(seleniumScrapingService))
      context.watch(r)
      router = router.addRoutee(r)
  }
}

object SeleniumWorkerRouter {

  def props(webScraperConfiguration: WebScraperConfiguration) =
    Props(new SeleniumWorkerRouter(webScraperConfiguration))
}
