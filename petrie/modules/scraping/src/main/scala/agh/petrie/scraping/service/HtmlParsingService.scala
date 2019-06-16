package agh.petrie.scraping.service

import agh.petrie.scraping.model.Configuration
import org.jsoup.Jsoup
import agh.petrie.scraping.web.AsyncScrapingService.Html

import scala.collection.JavaConverters._

class HtmlParsingService(urlRegexMatchingService: UrlRegexMatchingService) {

  def fetchUrls(html: Html, configuration: Configuration): Seq[String] = {
    val document = Jsoup.parse(html.body)
    val urls = document.select("a[href]")
    val absUrls = for {
      url <- urls.iterator().asScala
    } yield url.absUrl("href")
    absUrls
      .toSeq
      .filter(_ != "")
      .filter(urlRegexMatchingService.matchRegex(configuration.urlConfiguration.flatMap(_.regex.map(_.r))))
  }
}
