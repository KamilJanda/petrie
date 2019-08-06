package agh.petrie.scraping.actors.controllers

import agh.petrie.scraping.actors.receptionist.SimpleReceptionist
import agh.petrie.scraping.actors.receptionist.SimpleReceptionist.FetchedUrls
import agh.petrie.scraping.model.Configuration
import agh.petrie.scraping.service.ScraperResolverService
import akka.actor.{ActorRef, Props}

class SimpleController(
  scraperResolverService: ScraperResolverService,
  configuration:          Configuration
) extends BaseController(scraperResolverService, configuration) {

  override def onNegativeDepth(): Unit = {
    sender ! FetchedUrls(Set())
  }

  override def onFetchedUrl(url : String,responseTo: ActorRef): Unit = {}

  override def onCheckDone(children: Set[ActorRef], urls: Set[String], responseTo: ActorRef): Unit = {
    if ((children - sender).isEmpty) {
      responseTo ! SimpleReceptionist.FetchedUrls(urls)
      context.stop(self)
    } else {
      context.become(checkingUrls(children - sender, urls, responseTo))
    }
  }
}

object SimpleController{
  def props(scraperResolverService: ScraperResolverService, configuration: Configuration) =
    Props(new SimpleController(scraperResolverService, configuration))
}
