package agh.petrie.scraping

import agh.petrie.scraping.service.{HtmlParsingService, UrlRegexMatchingService}
import agh.petrie.scraping.web.{AsyncScrapingService, SeleniumScrapingService}
import com.ning.http.client.AsyncHttpClient
import com.softwaremill.macwire._
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.{ChromeDriver, ChromeOptions}

trait ScrapingModule {

  private[scraping] lazy val asyncHttpClient = wire[AsyncHttpClient]
  private[scraping] lazy val htmlParsingService = wire[HtmlParsingService]
  private[scraping] lazy val urlRegexMatchingService = wire[UrlRegexMatchingService]
  private[scraping] lazy val asyncScrapingService = wire[AsyncScrapingService]

  private[scraping] lazy val options = wire[ChromeOptions]
  private[scraping] lazy val driver: WebDriver = wire[ChromeDriver]
  private[scraping] lazy val seleniumScrapingService = wire[SeleniumScrapingService]
}
