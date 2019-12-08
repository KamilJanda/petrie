package agh.petrie.scraping.service

import agh.petrie.scraping.model.{DontScrap, FallbackScenario, ScrapingScenario}
import agh.petrie.scraping.service.HtmlParsingService.{Html, WebsiteContent}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

import scala.collection.JavaConverters._
import scala.util.matching.Regex

class HtmlParsingService(urlRegexMatchingService: UrlRegexMatchingService) {

  def fetchContent(
    html: Html,
    scenario: Either[FallbackScenario, ScrapingScenario],
    isTestScraping: Boolean
  ): WebsiteContent = {
    val document = Jsoup.parse(html.body)
    val absUrls  = fetchUrls(document, scenario)
    val urlRegex: Seq[Regex] =
      scenario.toSeq.flatMap(conf => conf.postScrapingConfiguration.urlConfiguration.map(_.regex.r))
    val resultUrls = absUrls
      .filter(_ != "")
      .filter(urlRegexMatchingService.matchRegex(urlRegex))
    val content = scenario.toOption.map(fetchContent(document, _)).getOrElse(Map.empty)
    WebsiteContent(
      scenario.toOption.map(_.name),
      content,
      if (isTestScraping) resultUrls.headOption.toList else resultUrls
    )
  }

  private def fetchContent(document: Document, scrapingScenario: ScrapingScenario): Map[String, String] = {
    val result = for {
      selector <- scrapingScenario.scrapingConfiguration.elementsToScrapContentFrom
      el = document.select(selector.selector)
    } yield selector.name -> el.text()
    result.toMap
  }

  private def fetchUrls(document: Document, scenario: Either[FallbackScenario, ScrapingScenario]): Seq[String] = {
    scenario match {
      case Right(scenario) if scenario.scrapingConfiguration.elementsToFetchUrlsFrom.nonEmpty =>
        fetchBySelector(document, scenario)
      case Right(scenario) if scenario.scrapingConfiguration.topicsToFetchUrlsFrom.nonEmpty =>
        fetchByTopic(document, scenario)
      case Left(DontScrap) =>
        Seq()
      case _ =>
        elementsToUrl(document.select("a[href]"))
    }
  }

  private def fetchBySelector(document: Document, scenario: ScrapingScenario): Seq[String] = {
    for {
      path <- scenario.scrapingConfiguration.elementsToFetchUrlsFrom.map(_.selector)
      el       = document.select(path)
      elements = el.select("a[href]")
      url <- elementsToUrl(elements)
    } yield url
  }

  private def elementsToUrl(elements: Elements): Seq[String] = {
    (for {
      url <- elements.iterator().asScala
    } yield url.absUrl("href")).toSeq
  }

  private def fetchByTopic(document: Document, scenario: ScrapingScenario): Seq[String] = {
    for {
      words <- scenario.scrapingConfiguration.topicsToFetchUrlsFrom.map(_.topicSelector)
      elements = document.select(s"a:contains($words)")
      url <- elementsToUrl(elements)
    } yield url
  }
}

object HtmlParsingService {
  final case class Html(body: String)

  final case class WebsiteContent(
    usedScenario: Option[String],
    content: Map[String, String],
    urls: Seq[String]
  )
}
