package agh.petrie.core.model.view

import play.api.libs.json.Json

case class FetchLinksRequest(
  url: String,
  depth: Int
)

object FetchLinksRequest {
  implicit lazy val format = Json.format[FetchLinksRequest]
}