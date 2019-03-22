package scraping.actors

import akka.actor.{Actor, ActorRef, Props}
import scraping.actors.Controller.{CheckDone, CheckUrl}
import scraping.service.HtmlParsingService
import scraping.web.AsyncScrapingService

class Controller(asyncScrapingService: AsyncScrapingService, htmlParsingService: HtmlParsingService) extends Actor {

  override def receive: Receive = {
    case message @ CheckUrl(_, depth) if depth >= 0 =>
      context.become(checkingUrls(Set.empty, Set.empty, sender))
      self ! message

    case CheckUrl(url, _) => sender ! Receptionist.UrlsFetched(Set(url))
  }


  def checkingUrls(children: Set[ActorRef], urls: Set[String], receptionist: ActorRef): Receive = {
    case CheckUrl(url, depth) if depth >= 0 =>
      val worker = context.actorOf(Getter.props(url, depth, asyncScrapingService, htmlParsingService))
      context.become(checkingUrls(children + worker, urls + url, receptionist))

    case CheckUrl(url, _) =>
      context.become(checkingUrls(children, urls + url, receptionist))

    case CheckDone =>
      if ((children - sender).isEmpty) {
        receptionist ! Receptionist.UrlsFetched(urls)
        context.stop(self)
      } else {
        context.become(checkingUrls(children - sender, urls, receptionist))
      }
  }

}


object Controller {
  def props(asyncScrapingService: AsyncScrapingService, htmlParsingService: HtmlParsingService) =
    Props(new Controller(asyncScrapingService, htmlParsingService))

  case class CheckUrl(url: String, depth: Int)
  case object CheckDone
}