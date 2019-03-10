package scraping.web

import org.openqa.selenium.WebDriver

class SeleniumScrapingService(driver: WebDriver) {

  def getUrlContent(url: String) = {
    driver.get(url)
    driver.getPageSource
  }
}
