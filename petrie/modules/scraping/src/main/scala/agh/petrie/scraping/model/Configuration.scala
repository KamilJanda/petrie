package agh.petrie.scraping.model

case class Configuration(
  rootScenario:          List[ScrapingScenario],
  maxSearchDepth:        Int,
  noScenarioFallback:    FallbackScenario,
  scrapingType:          ScrapingType
)

final case class ScrapingScenarioId(id: Long) extends AnyVal

class ScrapingScenario(
  val id:                        Option[ScrapingScenarioId],
  val name:                      String,
  val preScrapingConfiguration:  PreScrapingConfiguration,
  val scrapingConfiguration:     ScrapingConfiguration,
  val postScrapingConfiguration: PostScrapingConfiguration,
  var targetScenario:            Option[ScrapingScenario]
)

final case class PreScrapingConfiguration(
  elementsToClick: List[SelectorConfiguration]
)

object PreScrapingConfiguration{
  def empty() = PreScrapingConfiguration(List())
}

final case class ScrapingConfiguration(
  elementsToFetchUrlsFrom: List[SelectorConfiguration],
  elementsToScrapContentFrom: List[SelectorConfiguration]
)

object ScrapingConfiguration{
  def empty() = ScrapingConfiguration(List(), List())
}

final case class PostScrapingConfiguration(
  urlConfiguration: List[UrlConfiguration]
)

object PostScrapingConfiguration{
  def empty() = PostScrapingConfiguration(List())
}

final case class UrlConfiguration(
  regex: String
)

final case class SelectorConfiguration(
  selectorType: SelectorType,
  selector:     String
)
