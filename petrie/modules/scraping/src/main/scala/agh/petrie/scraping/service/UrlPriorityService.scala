package agh.petrie.scraping.service

import agh.petrie.scraping.actors.controllers.FrontierPriorityQueue.{Priority, StandardPriority}
import agh.petrie.scraping.model.UrlPriority

class UrlPriorityService {
  def getPriority(url: String, urlPriorities: List[UrlPriority]): Priority =
    urlPriorities
      .find(_.url.r.findFirstMatchIn(url).isDefined)
      .map(_.priority)
      .getOrElse(StandardPriority)

}
