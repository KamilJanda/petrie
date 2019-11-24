package agh.petrie.scraping.model

import agh.petrie.scraping.actors.controllers.FrontierPriorityQueue.Priority

case class Configuration(
  rootScenario: List[ScrapingScenario],
  urlPriorities: List[UrlPriority],
  maxSearchDepth: Int,
  noScenarioFallback: FallbackScenario,
  scrapingType: ScrapingType,
  isTestScraping: Boolean
)

final case class ScrapingScenarioId(id: Long) extends AnyVal

class ScrapingScenario(
  val id: Option[ScrapingScenarioId],
  val name: String,
  val preScrapingConfiguration: PreScrapingConfiguration,
  val scrapingConfiguration: ScrapingConfiguration,
  val postScrapingConfiguration: PostScrapingConfiguration,
  var targetScenario: Option[ScrapingScenario]
)

final case class PreScrapingConfiguration(
  preScrapingConfigurationElements: List[PreScrapingConfigurationElement]
)

object PreScrapingConfiguration {
  def empty() = PreScrapingConfiguration(List())
}

final case class ScrapingConfiguration(
  elementsToFetchUrlsFrom: List[SelectorConfiguration],
  elementsToScrapContentFrom: List[FetchDataSelectorConfiguration],
  topicsToFetchUrlsFrom: List[ScrapingTopic]
)

object ScrapingConfiguration {
  def empty() = ScrapingConfiguration(List(), List(), List())
}

final case class PostScrapingConfiguration(
  urlConfiguration: List[UrlConfiguration]
)

object PostScrapingConfiguration {
  def empty() = PostScrapingConfiguration(List())
}

final case class UrlConfiguration(
  regex: String
)

final case class SelectorConfiguration(
  selectorType: SelectorType,
  selector: String
)

final case class FetchDataSelectorConfiguration(
  name: String,
  selectorType: SelectorType,
  selector: String
)

final case class ScrapingTopic(
  topicType: TopicType,
  topicSelector: String
)

final case class UrlPriority(
  url: String,
  priority: Priority
)
