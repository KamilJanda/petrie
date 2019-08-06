package agh.petrie.core.model.view

import play.api.libs.json.Json
import agh.petrie.core.model.view.ConfigurationView._

case class FetchLinksRequest(
  url: String,
  configuration: ConfigurationView
)

object FetchLinksRequest {
  implicit lazy val format = Json.format[FetchLinksRequest]
}
