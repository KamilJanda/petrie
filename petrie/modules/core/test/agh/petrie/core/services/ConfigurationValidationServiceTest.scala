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

  it should "return error validation if url in url priority is empty" in {
    val result = new ConfigurationValidationService().validate(configurationWithInvalidUrlPriority)

    result should be(Left("Empty string is forbidden url in url priority"))
  }

  it should "return error validation for invalid priority type" in {
    val result = new ConfigurationValidationService().validate(configurationWithInvalidPriorityType)

    result should be(Left("Invalid priority type"))
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

  val urlPrioritiesView = UrlPrioritiesView(
    url = "example.org",
    priority = "HighPriority"
  )

  val urlPrioritiesViewWithEmptyUrl = UrlPrioritiesView(
    url = "",
    priority = "HighPriority"
  )

  val urlPrioritiesWithInvalidPriorityType = UrlPrioritiesView(
    url = "example.org",
    priority = "BadPriority"
  )

  val configurationWithTopicAndFocusedCrawlingConflict = ConfigurationView(
    scenarios = List(
      scenarioView.copy(scrapingConfiguration = invalidScrapingConfiguration),
      scenarioView.copy(name = "name2", scrapingConfiguration = invalidScrapingConfiguration),
      scenarioView.copy(name = "name3")
    ),
    urlPriorities = List(urlPrioritiesView),
    maxSearchDepth = 1,
    scrapAllIfNoScenario = true,
    scrapDynamically = true
  )

  val configuration = ConfigurationView(
    scenarios = List(scenarioView, scenarioView.copy(name = "name2")),
    urlPriorities = List(urlPrioritiesView),
    maxSearchDepth = 1,
    scrapAllIfNoScenario = true,
    scrapDynamically = true
  )

  val configurationWithInvalidUrlPriority =
    configuration.copy(
      urlPriorities = List(urlPrioritiesView, urlPrioritiesViewWithEmptyUrl)
    )

  val configurationWithInvalidPriorityType =
    configuration.copy(
      urlPriorities = List(urlPrioritiesView, urlPrioritiesWithInvalidPriorityType)
    )

}
