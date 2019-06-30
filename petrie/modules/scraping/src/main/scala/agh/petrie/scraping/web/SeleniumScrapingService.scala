package agh.petrie.scraping.web

import agh.petrie.scraping.model.Configuration
import agh.petrie.scraping.service.HtmlParsingService.Html
import com.softwaremill.macwire.wire
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.{ChromeDriver, ChromeOptions}

import scala.concurrent.{ExecutionContext, Future}

class SeleniumScrapingService {

  private[scraping] lazy val options = wire[ChromeOptions]
  private[scraping] lazy val driver: WebDriver = wire[ChromeDriver]

  def getUrlContent(url: String, configuration: Configuration)(implicit ec: ExecutionContext) = {
    Future{
      driver.get(url)
      Html(driver.getPageSource)
    }
  }
}
