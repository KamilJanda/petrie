package agh.petrie.scraping.model

sealed trait PreScrapingConfigurationElement

case class ElementToClick(selector: SelectorConfiguration) extends PreScrapingConfigurationElement
case class WaitTimeout(timeout: Int) extends PreScrapingConfigurationElement
case class ScrollToElement(selector: SelectorConfiguration) extends PreScrapingConfigurationElement
case class WriteToElement(selector: SelectorConfiguration, text: String) extends PreScrapingConfigurationElement
