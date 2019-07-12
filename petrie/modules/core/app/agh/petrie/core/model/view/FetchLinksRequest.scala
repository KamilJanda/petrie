package agh.petrie.core.model.view

import agh.petrie.scraping.model.{Configuration, UrlConfiguration, SelectorConfiguration}
import play.api.libs.json.Json

case class FetchLinksRequest(
  url:           String,
  depth:         Int,
  configuration: Configuration
)

object FetchLinksRequest {
  implicit lazy val urlConfig = Json.format[UrlConfiguration]
  implicit lazy val xpathConfig = Json.format[SelectorConfiguration]
  implicit lazy val config = Json.format[Configuration]
  implicit lazy val format = Json.format[FetchLinksRequest]
}