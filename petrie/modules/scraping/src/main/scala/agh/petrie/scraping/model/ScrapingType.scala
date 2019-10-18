package agh.petrie.scraping.model

sealed trait ScrapingType
case object DynamicScraping extends ScrapingType
case object AsyncScraping   extends ScrapingType
