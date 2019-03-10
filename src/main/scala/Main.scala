
import java.util.concurrent.Executor

import akka.actor.ActorSystem
import com.ning.http.client.AsyncHttpClient
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.{ChromeDriver, ChromeOptions}
import scraping.actors.WebScraper
import scraping.web.{AsyncScrapingService, SeleniumScrapingService}

import scala.concurrent._
import ExecutionContext.Implicits.global

class Main {

}

object Main {
  def main(args: Array[String]): Unit = {

//    val options = new ChromeOptions
//    options.addArguments("--headless")
//    val driver: WebDriver = new ChromeDriver(options)
//    val scp = new SeleniumScrapingService(driver)


    val httpClient = new AsyncHttpClient
    val async = new AsyncScrapingService(httpClient)

    val system = ActorSystem("testSystem")
    val firstRef = system.actorOf(WebScraper.props("https://www.google.com", async), "act")

    //async.getUrlContent("https://www.seleniumhq.org/docs/03_webdriver.jsp#webdriver-and-the-selenium-server").foreach(println)
    //val d = scp.getUrlContent("https://www.seleniumhq.org/docs/03_webdriver.jsp#webdriver-and-the-selenium-server")
    //println(d)
  }
}