package agh.petrie.scraping.service

import agh.petrie.scraping.model.{Configuration, DontScrap, FallbackScenario, ScrapAll, ScrapingScenario}
import agh.petrie.scraping.service.HtmlParsingService.Html
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import us.codecraft.xsoup.Xsoup

import scala.collection.JavaConverters._
import scala.util.matching.Regex

class HtmlParsingService(urlRegexMatchingService: UrlRegexMatchingService) {

  def fetchUrls(html: Html, scenario: Either[FallbackScenario, ScrapingScenario]): Seq[String] = {
    val document = Jsoup.parse(html.body)
    val absUrls = fetchUrls(document, scenario)
    val urlRegex: Seq[Regex] = scenario.toSeq.flatMap(conf => conf.postScrapingConfiguration.urlConfiguration.map(_.regex.r))
    absUrls
      .filter(_ != "")
      .filter(urlRegexMatchingService.matchRegex(urlRegex))
  }

  def fetchUrls(document: Document, scenario: Either[FallbackScenario, ScrapingScenario]): Seq[String] = {
    scenario match {
      case Right(scenario) if scenario.scrapingConfiguration.elementsToFetchUrlsFrom.nonEmpty =>
        fetchBySelector(document, scenario)
      case Left(DontScrap) =>
        Seq()
      case _ =>
        elementsToUrl(document.select("a[href]"))
    }
  }

  def fetchBySelector(document: Document, scenario: ScrapingScenario): Seq[String] = {
    for {
      path <- scenario.scrapingConfiguration.elementsToFetchUrlsFrom.map(_.selector)
      el = Xsoup.compile(path).evaluate(document).getElements
      elements = el.select("a[href]")
      url <- elementsToUrl(elements)
    } yield url
  }

  def elementsToUrl(elements: Elements): Seq[String] = {
    (for {
      url <- elements.iterator().asScala
    } yield url.absUrl("href")).toSeq
  }
}

object HtmlParsingService {
  case class Html(body: String)
}
