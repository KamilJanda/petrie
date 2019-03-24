package agh.petrie.core.model.view

import play.api.libs.json.{Json, Writes}

case class FetchedUrlsView(urls: Set[String])

object FetchedUrlsView {
  implicit lazy val writes: Writes[FetchedUrlsView] = Json.writes[FetchedUrlsView]
}
