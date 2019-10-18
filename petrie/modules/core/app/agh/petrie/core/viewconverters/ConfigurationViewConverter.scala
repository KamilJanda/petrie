package agh.petrie.core.viewconverters

import agh.petrie.core.model.view.{ConfigurationView, ScrapingScenarioView, SelectorConfigurationView}
import agh.petrie.scraping.model.{ScrapingScenario, _}
import io.scalaland.chimney.dsl._
import javax.inject.Singleton

@Singleton
class ConfigurationViewConverter {
  def fromView(configurationView: ConfigurationView): Configuration = {

    val configuration = configurationView.into[Configuration]
      .withFieldComputed(_.noScenarioFallback, view => if(view.scrapAllIfNoScenario) ScrapAll else DontScrap)
      .withFieldComputed(_.scrapingType, view => if(view.scrapDynamically) DynamicScraping else AsyncScraping)
      .withFieldComputed(_.rootScenario, config => getScenarios(config))
      .transform

    if(configuration.rootScenario.isEmpty) {
      configuration.copy(rootScenario = List(new ScrapingScenario(
        None,
        "default",
        PreScrapingConfiguration.empty,
        ScrapingConfiguration.empty,
        PostScrapingConfiguration.empty,
        None
      )))
    }
    else {
      configuration
    }
  }

  private def getScenarios(configurationView: ConfigurationView) = {
    val scenarioWithTarget = configurationView.scenarios.map(
      scenario => (scenario.targetScenario, toScenarioWithoutTarget(scenario))
    )
    val scenarios = scenarioWithTarget.map(_._2)
    scenarioWithTarget.foreach {
      case (target, scenario) => scenario.targetScenario = target.flatMap(targetName => scenarios.find(_.name == targetName))
    }
    scenarios.filter(scenario => configurationView.scenarios.find(_.name == scenario.name).map(_.isRootScenario).getOrElse(false))
  }

  private def toScenarioWithoutTarget(scenarioView: ScrapingScenarioView): ScrapingScenario = {
    new ScrapingScenario(
      scenarioView.id,
      scenarioView.name,
      scenarioView.preScrapingConfiguration.into[PreScrapingConfiguration].withFieldComputed(_.elementsToClick,  _.elementsToClick.map(toSelectorConfiguration)).transform,
      scenarioView.scrapingConfiguration.into[ScrapingConfiguration]
        .withFieldComputed(_.elementsToFetchUrlsFrom, _.elementsToFetchUrlsFrom.map(toSelectorConfiguration))
        .withFieldComputed(_.elementsToScrapContentFrom, _.elementsToScrapContentFrom.map(toSelectorConfiguration))
        .transform,
      scenarioView.postScrapingConfiguration.into[PostScrapingConfiguration].withFieldComputed(_.urlConfiguration,   _.urlConfiguration.map(_.transformInto[UrlConfiguration])).transform,
      None
    )
  }

  private def toSelectorConfiguration(selectorConfigurationView: SelectorConfigurationView) = {
    selectorConfigurationView.into[SelectorConfiguration]
      .withFieldComputed(_.selectorType, view => if(view.isXpathSelector) XpathSelector else CssSelector)
      .transform
  }

}
