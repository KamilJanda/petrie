package agh.petrie.scraping.service

import agh.petrie.scraping.model.Configuration
import agh.petrie.scraping.service.HtmlParsingService.Html
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import us.codecraft.xsoup.Xsoup

import scala.collection.JavaConverters._

class HtmlParsingService(urlRegexMatchingService: UrlRegexMatchingService) {

  def fetchUrls(html: Html, configuration: Configuration): Seq[String] = {
    val document = Jsoup.parse(html.body)
    val absUrls = fetchUrls(document, configuration)
    absUrls
      .filter(_ != "")
      .filter(urlRegexMatchingService.matchRegex(configuration.urlConfiguration.map(_.regex.r)))
  }

  def fetchUrls(document: Document, configuration: Configuration): Seq[String] = {
    if (configuration.selectorConfiguration.isEmpty) {
      elementsToUrl(document.select("a[href]"))
    } else {
      fetchBySelector(document, configuration)
    }
  }

  def fetchBySelector(document: Document, configuration: Configuration): Seq[String] = {
    for {
      path <- configuration.selectorConfiguration.map(_.selector)
      el =  Xsoup.compile(path).evaluate(document).getElements
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

object HtmlParsingService{
  case class Html(body: String)
}
