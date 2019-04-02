package agh.petrie.scraping.actors

import akka.actor.{Actor, ActorRef, Props}
import agh.petrie.scraping.actors.Controller.{CheckDone, CheckUrl, CheckUrlAsync}
import agh.petrie.scraping.api.BasicScrapingApi
import agh.petrie.scraping.api.BasicScrapingApi.{Complete, Message, Protocol}
import agh.petrie.scraping.service.HtmlParsingService
import agh.petrie.scraping.web.AsyncScrapingService

class Controller(asyncScrapingService: AsyncScrapingService, htmlParsingService: HtmlParsingService) extends Actor {

  override def receive: Receive = {
    case message @ CheckUrl(_, depth) if depth >= 0 =>
      context.become(checkingUrls(Set.empty, Set.empty, sender))
      self ! message

    case message @ CheckUrlAsync(_, depth, ref) if depth >= 0 =>
      context.become(checkingUrlsAsync(Set.empty, Set.empty, ref))
      self ! message

    case CheckUrl(url, _) => sender ! Receptionist.FetchedUrls(Set(url))
  }


  def checkingUrls(children: Set[ActorRef], urls: Set[String], receptionist: ActorRef): Receive = {
    case CheckUrl(url, depth) if depth >= 0 && url != "" =>
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
    stream: akka.actor.typed.ActorRef[Protocol]
  ): Receive = {
    case CheckUrl(url, depth) if depth >= 0 =>
      println(url)
      val worker = context.actorOf(Getter.props(url, depth, asyncScrapingService, htmlParsingService))
      stream ! Message(url)
      context.become(checkingUrlsAsync(children + worker, urls + url, stream))

    case CheckUrl(url, _) =>
      println(url)
      stream ! Message(url)
      context.become(checkingUrlsAsync(children, urls + url, stream))

    case CheckDone =>
      if ((children - sender).isEmpty) {
        stream ! Complete
        context.stop(self)
      } else {
        context.become(checkingUrlsAsync(children - sender, urls, stream))
      }
  }
}


object Controller {
  def props(asyncScrapingService: AsyncScrapingService, htmlParsingService: HtmlParsingService) =
    Props(new Controller(asyncScrapingService, htmlParsingService))

  case class CheckUrl(url: String, depth: Int)
  case class CheckUrlAsync(url: String, depth: Int, streamRef: akka.actor.typed.ActorRef[BasicScrapingApi.Protocol])
  case object CheckDone
}