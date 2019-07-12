package agh.petrie.scraping

case class WebScraperConfiguration(
                                    seleniumDriversCount: Int,
                                    asyncGetterTimeoutInSeconds: Int,
                                    dynamicGetterTimeoutInSeconds: Int,
                                  )
