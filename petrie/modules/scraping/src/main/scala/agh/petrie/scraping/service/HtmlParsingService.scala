package agh.petrie.scraping.service

import agh.petrie.scraping.model.{Configuration, DontScrap, FallbackScenario, ScrapAll, ScrapingScenario}
import agh.petrie.scraping.service.HtmlParsingService.{Html, WebsiteContent}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import us.codecraft.xsoup.Xsoup

import scala.collection.JavaConverters._
import scala.util.matching.Regex

class HtmlParsingService(urlRegexMatchingService: UrlRegexMatchingService) {

  def fetchContent(html: Html, scenario: Either[FallbackScenario, ScrapingScenario]): WebsiteContent = {
    val document = Jsoup.parse(html.body)
    val absUrls = fetchUrls(document, scenario)
    val urlRegex: Seq[Regex] = scenario.toSeq.flatMap(conf => conf.postScrapingConfiguration.urlConfiguration.map(_.regex.r))
    val resultUrls = absUrls
      .filter(_ != "")
      .filter(urlRegexMatchingService.matchRegex(urlRegex))
    val content = scenario.toOption.flatMap(fetchContent(document, _))
    WebsiteContent(content, resultUrls)
  }

  private def fetchContent(document: Document, scrapingScenario: ScrapingScenario): Option[String] = {
    val text = for {
      path <- scrapingScenario.scrapingConfiguration.elementsToScrapContentFrom.map(_.selector)
      el = document.select(path)
    } yield el.text()
    text.reduceOption(_ + _)
  }

  private def fetchUrls(document: Document, scenario: Either[FallbackScenario, ScrapingScenario]): Seq[String] = {
    scenario match {
      case Right(scenario) if scenario.scrapingConfiguration.elementsToFetchUrlsFrom.nonEmpty =>
        fetchBySelector(document, scenario)
      case Left(DontScrap) =>
        Seq()
      case _ =>
        elementsToUrl(document.select("a[href]"))
    }
  }

  private def fetchBySelector(document: Document, scenario: ScrapingScenario): Seq[String] = {
    for {
      path <- scenario.scrapingConfiguration.elementsToFetchUrlsFrom.map(_.selector)
      el = document.select(path)
      elements = el.select("a[href]")
      url <- elementsToUrl(elements)
    } yield url
  }

  private def elementsToUrl(elements: Elements): Seq[String] = {
    (for {
      url <- elements.iterator().asScala
    } yield url.absUrl("href")).toSeq
  }
}

object HtmlParsingService {
  final case class Html(body: String)

  final case class WebsiteContent(
    text: Option[String],
    urls: Seq[String]
  )
}
