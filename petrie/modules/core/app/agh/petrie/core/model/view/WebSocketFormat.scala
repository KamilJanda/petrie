package agh.petrie.core.model.view


import agh.petrie.core.viewconverters.WebsocketConverterActor.GetUrlsView
import agh.petrie.scraping.api.BasicScrapingApi._
import agh.petrie.core.model.view.ConfigurationView._
import agh.petrie.scraping.actors.receptionist.SimpleReceptionist.WebsiteData
import play.api.libs.json.Json
import play.api.mvc.WebSocket.MessageFlowTransformer

object WebSocketFormat {
  import agh.petrie.core.model.view.FetchLinksRequest._

  implicit lazy val websiteData = Json.format[WebsiteData]
  implicit lazy val message = Json.format[Message]
  implicit lazy val complete = Json.format[Complete.type]
  implicit lazy val getUrlsAsync = Json.format[GetUrlsView]
  implicit lazy val protocol = Json.format[Protocol]

  implicit val messageFlowTransformer = MessageFlowTransformer.jsonMessageFlowTransformer[GetUrlsView, Protocol]
}
