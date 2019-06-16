package agh.petrie.scraping.model

case class Configuration(
  urlConfiguration: List[UrlConfiguration]
)

case class UrlConfiguration(
  regex: Option[String]
)