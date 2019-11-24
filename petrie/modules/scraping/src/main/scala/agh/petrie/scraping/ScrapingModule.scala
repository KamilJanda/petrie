package agh.petrie.scraping

import agh.petrie.scraping.service._
import agh.petrie.scraping.web.AsyncScrapingService
import akka.actor.ActorSystem
import com.ning.http.client.AsyncHttpClient
import com.softwaremill.macwire._

trait ScrapingModule {

  def actorSystem: ActorSystem
  def webScraperConfiguration: WebScraperConfiguration

  private[scraping] lazy val asyncHttpClient         = wire[AsyncHttpClient]
  private[scraping] lazy val htmlParsingService      = wire[HtmlParsingService]
  private[scraping] lazy val urlRegexMatchingService = wire[UrlRegexMatchingService]
  private[scraping] lazy val asyncScrapingService    = wire[AsyncScrapingService]
  private[scraping] lazy val timeProvider            = wire[SimpleTimeProvider]

  private[scraping] lazy val scraperResolverService = wire[ScraperResolverService]
  private[scraping] lazy val throttlingService      = wire[ThrottlingService]
}
