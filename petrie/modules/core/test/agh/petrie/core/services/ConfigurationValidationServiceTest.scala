package agh.petrie.core.services

import agh.petrie.core.model.view._
import org.scalatest.{FlatSpec, Matchers}

class ConfigurationValidationServiceTest extends FlatSpec with Matchers {

  import ConfigurationValidationServiceTest._

  it should "return valid for correct configuration" in {
    val result = new ConfigurationValidationService().validate(configuration)

    result should be(Right(()))
  }

  it should "return error validation if both Topic and Focused crawling are defined" in {
    val result = new ConfigurationValidationService().validate(configurationWithTopicAndFocusedCrawlingConflict)

    result should be(Left("Scraping configuration must be either Topical or Focused"))
  }

}

object ConfigurationValidationServiceTest {

  val invalidScrapingConfiguration = ScrapingConfigurationView(
    List(SelectorConfigurationView(isXpathSelector = false, "selector")),
    List.empty,
    List(ScrapingTopicView("keyWords", "word"))
  )

  val scenarioView = ScrapingScenarioView(
    None,
    "name",
    PreScrapingConfigurationView(List.empty),
    ScrapingConfigurationView(
      List(SelectorConfigurationView(isXpathSelector = false, "selector")),
      List.empty,
      List.empty
    ),
    PostScrapingConfigurationView(List.empty),
    None,
    isRootScenario = true
  )

  val configurationWithTopicAndFocusedCrawlingConflict = ConfigurationView(
    List(
      scenarioView.copy(scrapingConfiguration = invalidScrapingConfiguration),
      scenarioView.copy(name = "name2", scrapingConfiguration = invalidScrapingConfiguration),
      scenarioView.copy(name = "name3")
    ),
    1,
    scrapAllIfNoScenario = true,
    scrapDynamically = true
  )

  val configuration = ConfigurationView(
    List(scenarioView, scenarioView.copy(name = "name2")),
    1,
    scrapAllIfNoScenario = true,
    scrapDynamically = true
  )

}
