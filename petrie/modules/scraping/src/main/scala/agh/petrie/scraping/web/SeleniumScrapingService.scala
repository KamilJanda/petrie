package agh.petrie.scraping.web

import agh.petrie.scraping.model.{Configuration, CssSelector, PreScrapingConfiguration, XpathSelector}
import agh.petrie.scraping.service.HtmlParsingService.Html
import com.softwaremill.macwire.wire
import org.openqa.selenium.{By, Keys, WebDriver}
import org.openqa.selenium.chrome.{ChromeDriver, ChromeOptions}
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait

import scala.concurrent.{ExecutionContext, Future}

class SeleniumScrapingService {

  private[scraping] lazy val options           = wire[ChromeOptions]
  private[scraping] lazy val driver: WebDriver = wire[ChromeDriver]

  def getUrlContent(
    url: String,
    preScrapingConfiguration: Option[PreScrapingConfiguration]
  )(implicit ec: ExecutionContext) = {
    Future {
      driver.get(url)
      preScrapingConfiguration.foreach(
        config =>
          config.elementsToClick.foreach(
            element =>
              if (element.selectorType == CssSelector) {
                val wait = new WebDriverWait(driver, 10)
                try {
                  wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(element.selector)))
                  val el = driver.findElement(By.cssSelector(element.selector))
                  //println("clicking element of size: " + el.getSize)
                  el.click()
                } catch {
                  case e => println(e)
                }
              } else {
                driver.findElement(By.xpath(element.selector)).click()
              }
          )
      )
      Html(driver.getPageSource)
    }
  }
}
