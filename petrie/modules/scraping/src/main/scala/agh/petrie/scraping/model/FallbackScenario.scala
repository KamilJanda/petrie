package agh.petrie.scraping.model

sealed trait FallbackScenario
case object DontScrap extends FallbackScenario
case object ScrapAll extends FallbackScenario