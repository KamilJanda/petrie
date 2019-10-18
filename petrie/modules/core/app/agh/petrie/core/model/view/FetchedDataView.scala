package agh.petrie.core.model.view

import play.api.libs.json.{Json, Writes}

case class FetchedDataView(
  result: Set[WebsiteDataView]
)

case class WebsiteDataView(
  url: String,
  content: Option[String]
)

object FetchedDataView {
  implicit lazy val writes: Writes[FetchedDataView] = Json.writes[FetchedDataView]
  implicit lazy val websiteDataWrites: Writes[WebsiteDataView] = Json.writes[WebsiteDataView]
}
