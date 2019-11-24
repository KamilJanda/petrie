package agh.petrie.scraping

case class WebScraperConfiguration(
  seleniumDriversCount: Int,
  asyncScraperTimeoutInSeconds: Int,
  dynamicScraperTimeoutInSeconds: Int,
  throttlingDelayTimeInSeconds: Int
)
