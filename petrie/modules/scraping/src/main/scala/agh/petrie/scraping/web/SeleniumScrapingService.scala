package agh.petrie.scraping.web

import agh.petrie.scraping.model.{Configuration, CssSelector, ElementToClick, PreScrapingConfiguration, PreScrapingConfigurationElement, ScrollToElement, SelectorConfiguration, WaitTimeout, WriteToElement, XpathSelector}
import agh.petrie.scraping.service.HtmlParsingService.Html
import com.softwaremill.macwire.wire
import org.openqa.selenium.{By, Keys, WebDriver}
import org.openqa.selenium.chrome.{ChromeDriver, ChromeOptions}
import org.openqa.selenium.By
import org.openqa.selenium.interactions.Actions
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
      preScrapingConfiguration.foreach(_.preScrapingConfigurationElements.foreach(runActionForPreScrapingConfiguration))
      Html(driver.getPageSource)
    }
  }

  private def runActionForPreScrapingConfiguration(preScrapingConfigurationElement: PreScrapingConfigurationElement) = {
    preScrapingConfigurationElement match {
      case ElementToClick(selector: SelectorConfiguration) => clickElement(selector)
      case WaitTimeout(timeout: Int) => webDriverWait(timeout)
      case ScrollToElement(selector: SelectorConfiguration) => scrollToElement(selector)
      case WriteToElement(selector: SelectorConfiguration, text: String) => writeToElement(selector, text)
    }
  }

  private def clickElement(configuration: SelectorConfiguration) = {
      if (configuration.selectorType == CssSelector) {
        val wait = new WebDriverWait(driver, 10)
        try {
          wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(configuration.selector)))
          val el = driver.findElement(By.cssSelector(configuration.selector))
          //println("clicking element of size: " + el.getSize)
          el.click()
        } catch {
          case e => println(e)
        }
      } else {
        driver.findElement(By.xpath(configuration.selector)).click()
      }
  }

  private def webDriverWait(timeout: Int) = {
    Thread.sleep(timeout * 1000)
  }

  private def scrollToElement(configuration: SelectorConfiguration) = {
    if (configuration.selectorType == CssSelector) {
      val wait = new WebDriverWait(driver, 10)
      try {
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(configuration.selector)))
        val el = driver.findElement(By.cssSelector(configuration.selector))
//        println("clicking element of size: " + el.getSize)
        val actions = new Actions(driver);
        actions.moveToElement(el);
        actions.perform();
      } catch {
        case e => println(e)
      }
    }
  }

  private def writeToElement(configuration: SelectorConfiguration, text: String) = {
    if (configuration.selectorType == CssSelector) {
      val wait = new WebDriverWait(driver, 10)
      try {
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(configuration.selector)))
        val el = driver.findElement(By.cssSelector(configuration.selector))
        //println("clicking element of size: " + el.getSize)
        el.click()
        el.sendKeys(text)
      } catch {
        case e => println(e)
      }
    }
  }
}
