package scraping.service

import org.jsoup.Jsoup
import scraping.web.AsyncScrapingService.Html
import scala.collection.JavaConverters._

class HtmlParsingService {

  def fetchUrls(html: Html): Seq[String] = {
    val document = Jsoup.parse(html.body)
    val urls = document.select("a[href]")
    val absUrls = for {
      url <- urls.iterator().asScala
    } yield url.absUrl("href")
    absUrls.toSeq
  }
}
