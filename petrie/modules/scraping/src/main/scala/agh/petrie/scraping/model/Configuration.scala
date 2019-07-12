package agh.petrie.scraping.model

case class Configuration(
  urlConfiguration:      List[UrlConfiguration],
  selectorConfiguration: List[SelectorConfiguration],
  searchDynamically:     Boolean
)

case class UrlConfiguration(
  regex: String
)

case class SelectorConfiguration(
  selector: String
)