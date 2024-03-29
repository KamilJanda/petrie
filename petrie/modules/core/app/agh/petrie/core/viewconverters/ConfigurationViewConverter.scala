package agh.petrie.core.viewconverters

import agh.petrie.core.model.view._
import agh.petrie.scraping.actors.controllers.FrontierPriorityQueue.{HighPriority, LowPriority, StandardPriority}
import agh.petrie.scraping.model.{ScrapingScenario, _}
import io.scalaland.chimney.dsl._
import javax.inject.Singleton

@Singleton
class ConfigurationViewConverter {
  def fromView(configurationView: ConfigurationView, isTestScraping: Boolean): Configuration = {

    val configuration = configurationView
      .into[Configuration]
      .withFieldComputed(_.noScenarioFallback, view => if (view.scrapAllIfNoScenario) ScrapAll else DontScrap)
      .withFieldComputed(_.scrapingType, view => if (view.scrapDynamically) DynamicScraping else AsyncScraping)
      .withFieldComputed(_.rootScenario, config => getScenarios(config))
      .withFieldComputed(_.urlPriorities, config => config.urlPriorities.map(toUrlPriorities))
      .withFieldConst(_.isTestScraping, isTestScraping)
      .transform

    if (configuration.rootScenario.isEmpty) {
      configuration.copy(
        rootScenario = List(
          new ScrapingScenario(
            None,
            "default",
            PreScrapingConfiguration.empty,
            ScrapingConfiguration.empty,
            PostScrapingConfiguration.empty,
            None
          )
        )
      )
    } else {
      configuration
    }
  }

  private def getScenarios(configurationView: ConfigurationView) = {
    val scenarioWithTarget = configurationView.scenarios.map(
      scenario => (scenario.targetScenario, toScenarioWithoutTarget(scenario))
    )
    val scenarios = scenarioWithTarget.map(_._2)
    scenarioWithTarget.foreach {
      case (target, scenario) =>
        scenario.targetScenario = target.flatMap(targetName => scenarios.find(_.name == targetName))
    }
    scenarios.filter(
      scenario => configurationView.scenarios.find(_.name == scenario.name).map(_.isRootScenario).getOrElse(false)
    )
  }

  private def toScenarioWithoutTarget(scenarioView: ScrapingScenarioView): ScrapingScenario = {
    new ScrapingScenario(
      scenarioView.id,
      scenarioView.name,
      scenarioView.preScrapingConfiguration
        .into[PreScrapingConfiguration]
        .withFieldComputed(
          _.preScrapingConfigurationElements,
          _.preScrapingConfigurationElementsViews.map(toPreScrapingConfig)
        )
        .transform,
      scenarioView.scrapingConfiguration
        .into[ScrapingConfiguration]
        .withFieldComputed(_.elementsToFetchUrlsFrom, _.elementsToFetchUrlsFrom.map(toSelectorConfiguration))
        .withFieldComputed(
          _.elementsToScrapContentFrom,
          _.elementsToScrapContentFrom.map(toFetchDataSelectorConfiguration)
        )
        .withFieldComputed(_.topicsToFetchUrlsFrom, _.topicsToFetchUrlsFrom.map(toScrapingTopic))
        .transform,
      scenarioView.postScrapingConfiguration
        .into[PostScrapingConfiguration]
        .withFieldComputed(_.urlConfiguration, _.urlConfiguration.map(_.transformInto[UrlConfiguration]))
        .transform,
      None
    )
  }

  private def toPreScrapingConfig(
    preScrapingConfigurationElementView: PreScrapingConfigurationElementView
  ): PreScrapingConfigurationElement =
    preScrapingConfigurationElementView match {
      case ElementToClickView(selector: SelectorConfigurationView, _) =>
        ElementToClick(toSelectorConfiguration(selector))
      case WaitTimeoutView(timeout: Int, _) =>
        WaitTimeout(timeout)
      case ScrollToElementView(selector: SelectorConfigurationView, _) =>
        ScrollToElement(toSelectorConfiguration(selector))
      case WriteToElementView(selector: SelectorConfigurationView, text: String, _) =>
        WriteToElement(toSelectorConfiguration(selector), text)
    }

  private def toSelectorConfiguration(selectorConfigurationView: SelectorConfigurationView) = {
    selectorConfigurationView
      .into[SelectorConfiguration]
      .withFieldComputed(_.selectorType, view => if (view.isXpathSelector) XpathSelector else CssSelector)
      .transform
  }

  private def toFetchDataSelectorConfiguration(
    fetchDataSelectorConfigurationView: FetchDataSelectorConfigurationView
  ) = {
    fetchDataSelectorConfigurationView
      .into[FetchDataSelectorConfiguration]
      .withFieldComputed(_.selectorType, view => if (view.isXpathSelector) XpathSelector else CssSelector)
      .transform
  }

  private def toScrapingTopic(scrapingTopicView: ScrapingTopicView): ScrapingTopic = {
    scrapingTopicView
      .into[ScrapingTopic]
      .withFieldComputed(_.topicType, view => if (view.topicType == "keyWord") KeyWord else Sentence)
      .transform
  }

  private def toUrlPriorities(urlPrioritiesView: UrlPrioritiesView): UrlPriority = {
    urlPrioritiesView
      .into[UrlPriority]
      .withFieldComputed(
        _.priority,
        view =>
          view.priority match {
            case "HighPriority"     => HighPriority
            case "StandardPriority" => StandardPriority
            case "LowPriority"      => LowPriority
          }
      )
      .transform
  }
}
