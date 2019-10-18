package agh.petrie.core.model.view

import agh.petrie.scraping.model._
import play.api.libs.json.Json

case class ConfigurationView(
  scenarios: List[ScrapingScenarioView],
  maxSearchDepth: Int,
  scrapAllIfNoScenario: Boolean,
  scrapDynamically: Boolean
)

final case class ScrapingScenarioView(
  id: Option[ScrapingScenarioId],
  name: String,
  preScrapingConfiguration: PreScrapingConfigurationView,
  scrapingConfiguration: ScrapingConfigurationView,
  postScrapingConfiguration: PostScrapingConfigurationView,
  targetScenario: Option[String],
  isRootScenario: Boolean
)

final case class PreScrapingConfigurationView(
  elementsToClick: List[SelectorConfigurationView]
)

final case class ScrapingConfigurationView(
  elementsToFetchUrlsFrom: List[SelectorConfigurationView],
  elementsToScrapContentFrom: List[SelectorConfigurationView]
)

final case class PostScrapingConfigurationView(
  urlConfiguration: List[UrlConfigurationView]
)

final case class UrlConfigurationView(
  regex: String
)

final case class SelectorConfigurationView(
  isXpathSelector: Boolean,
  selector: String
)

object ConfigurationView {
  implicit lazy val idFormat                            = Json.format[ScrapingScenarioId]
  implicit lazy val urlConfigurationViewFormat          = Json.format[UrlConfigurationView]
  implicit lazy val selectorConfigurationViewFormat     = Json.format[SelectorConfigurationView]
  implicit lazy val preScrapingConfigurationViewFormat  = Json.format[PreScrapingConfigurationView]
  implicit lazy val scrapingConfigurationViewFormat     = Json.format[ScrapingConfigurationView]
  implicit lazy val postScrapingConfigurationViewFormat = Json.format[PostScrapingConfigurationView]
  implicit lazy val scenarioFormat                      = Json.format[ScrapingScenarioView]
  implicit lazy val configurationFormat                 = Json.format[ConfigurationView]
}
