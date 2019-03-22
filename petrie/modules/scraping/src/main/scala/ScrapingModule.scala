import com.ning.http.client.AsyncHttpClient
import scraping.service.HtmlParsingService
import com.softwaremill.macwire._
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.{ChromeDriver, ChromeOptions}
import scraping.web.{AsyncScrapingService, SeleniumScrapingService}

trait ScrapingModule {

  lazy val asyncHttpClient = wire[AsyncHttpClient]
  lazy val htmlParsingService = wire[HtmlParsingService]
  lazy val asyncScrapingService = wire[AsyncScrapingService]

  lazy val options = wire[ChromeOptions]
  lazy val driver: WebDriver = wire[ChromeDriver]
  lazy val seleniumScrapingService = wire[SeleniumScrapingService]
}
