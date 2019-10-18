package agh.petrie.core.services

import agh.petrie.common.ValidationUtils._
import agh.petrie.core.model.view.ConfigurationView
import cats.data.Validated
import cats.implicits._
import javax.inject.Singleton

@Singleton
class ConfigurationValidationService {

  def validate(configurationView: ConfigurationView): Either[String, Unit] = {
    List(
      validateNoRootScenario(configurationView),
      validateTargetDoesNotExists(configurationView),
      validateEmptyScenarioName(configurationView),
      validateDuplicateScenarioName(configurationView)
    ).combineAll.toEither
  }

  private def validateNoRootScenario(configurationView: ConfigurationView): Validated[String, Unit] = {
    val rootScenarioExists = configurationView.scenarios.exists(_.isRootScenario)
    rootScenarioExists.trueOrMessage("No root Scenario defined")
  }

  private def validateTargetDoesNotExists(configurationView: ConfigurationView): Validated[String, Unit] = {
    val scenarios = configurationView.scenarios
    val allTargetsExists = scenarios.forall(
      scenario => scenario.targetScenario.isEmpty || scenarios.exists(_.name == scenario.targetScenario.get)
    )
    allTargetsExists.trueOrMessage("Unknown target Scenario defined")
  }

  private def validateEmptyScenarioName(configurationView: ConfigurationView): Validated[String, Unit] = {
    val allScenariosWithNoneEmptyName = configurationView.scenarios.forall(_.name.replaceAll("\\s", "") != "")
    allScenariosWithNoneEmptyName.trueOrMessage("Empty string is forbidden scenario name")
  }

  private def validateDuplicateScenarioName(configurationView: ConfigurationView): Validated[String, Unit] = {
    val scenarioNames               = configurationView.scenarios.map(_.name)
    val allScenariosWithUniqueNames = scenarioNames.distinct.size == scenarioNames.size
    allScenariosWithUniqueNames.trueOrMessage("Duplicate scenario name")
  }

}
