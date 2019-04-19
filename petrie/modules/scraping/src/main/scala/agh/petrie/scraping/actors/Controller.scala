package agh.petrie.scraping.actors

import agh.petrie.scraping.actors.Controller.{CheckDone, CheckUrl, CheckUrlAsync}
import agh.petrie.scraping.api.BasicScrapingApi.{Complete, Message}
import agh.petrie.scraping.service.HtmlParsingService
import agh.petrie.scraping.web.AsyncScrapingService
import akka.actor.{Actor, ActorRef, PoisonPill, Props}

class Controller(asyncScrapingService: AsyncScrapingService, htmlParsingService: HtmlParsingService) extends Actor {

  override def receive: Receive = {
    case message @ CheckUrl(_, depth) if depth >= 0 =>
      context.become(checkingUrls(Set.empty, Set.empty, sender))
      self ! message

    case CheckUrlAsync(url, depth, ref) if depth >= 0 =>
      context.become(checkingUrlsAsync(Set.empty, Set.empty, ref))
      self ! CheckUrl(url, depth)

    case CheckUrl(url, _) => sender ! Receptionist.FetchedUrls(Set(url))

  }


  def checkingUrls(children: Set[ActorRef], urls: Set[String], receptionist: ActorRef): Receive = {
    case CheckUrl(url, depth) if depth >= 0 =>
      val worker = context.actorOf(Getter.props(url, depth, asyncScrapingService, htmlParsingService))
      context.become(checkingUrls(children + worker, urls + url, receptionist))

    case CheckUrl(url, _) =>
      context.become(checkingUrls(children, urls + url, receptionist))

    case CheckDone =>
      if ((children - sender).isEmpty) {
        receptionist ! Receptionist.FetchedUrls(urls)
        context.stop(self)
      } else {
        context.become(checkingUrls(children - sender, urls, receptionist))
      }
  }

  def checkingUrlsAsync(
    children: Set[ActorRef],
    urls: Set[String],
    socketActor: ActorRef
  ): Receive = {
    case CheckUrl(url, depth) if depth >= 0 =>
      val worker = context.actorOf(Getter.props(url, depth, asyncScrapingService, htmlParsingService))
      socketActor ! Message(url)
      context.become(checkingUrlsAsync(children + worker, urls + url, socketActor))

    case CheckUrl(url, _) =>
      socketActor ! Message(url)
      context.become(checkingUrlsAsync(children, urls + url, socketActor))

    case CheckDone =>
      if ((children - sender).isEmpty) {
        socketActor ! Complete
        socketActor ! PoisonPill
        context.stop(self)
      } else {
        context.become(checkingUrlsAsync(children - sender, urls, socketActor))
      }
  }
}


object Controller {
  def props(asyncScrapingService: AsyncScrapingService, htmlParsingService: HtmlParsingService) =
    Props(new Controller(asyncScrapingService, htmlParsingService))

  case class CheckUrl(url: String, depth: Int)
  case class CheckUrlAsync(url: String, depth: Int, streamRef: ActorRef)
  case object CheckDone
}