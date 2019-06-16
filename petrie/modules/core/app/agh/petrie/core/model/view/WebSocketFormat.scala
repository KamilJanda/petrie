package agh.petrie.core.model.view

import agh.petrie.scraping.actors.AsyncReceptionist.GetUrlsAsync
import agh.petrie.scraping.api.BasicScrapingApi._
import play.api.libs.json.Json
import play.api.mvc.WebSocket.MessageFlowTransformer


object WebSocketFormat {
  import agh.petrie.core.model.view.FetchLinksRequest._

  implicit lazy val message = Json.format[Message]
  implicit lazy val complete = Json.format[Complete.type]
  implicit lazy val getUrlsAsync =  Json.format[GetUrlsAsync]
  implicit lazy val protocol = Json.format[Protocol]

  implicit val messageFlowTransformer = MessageFlowTransformer.jsonMessageFlowTransformer[GetUrlsAsync, Protocol]
}
