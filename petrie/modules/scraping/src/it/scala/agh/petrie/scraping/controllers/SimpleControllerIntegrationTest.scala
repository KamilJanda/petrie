package agh.petrie.scraping.controllers

import agh.petrie.scraping.WebScraperConfiguration
import agh.petrie.scraping.actors.controllers.BaseController.StartScraping
import agh.petrie.scraping.actors.controllers.SimpleController
import agh.petrie.scraping.actors.receptionist.SimpleReceptionist.{FetchedData, WebsiteData}
import agh.petrie.scraping.model._
import agh.petrie.scraping.service.HtmlParsingService.Html
import agh.petrie.scraping.service.ThrottlingService.ScheduledVisitJournal
import agh.petrie.scraping.service._
import agh.petrie.scraping.web.AsyncScrapingService
import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit}
import com.ning.http.client.AsyncHttpClient
import com.softwaremill.macwire.wire
import com.typesafe.config.ConfigFactory
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchersSugar
import org.mockito.BDDMockito.given
import org.scalatest.junit.JUnitRunner
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike}

import scala.concurrent.Future

@RunWith(classOf[JUnitRunner])
class SimpleControllerIntegrationTest
  extends TestKit(ActorSystem("SimpleControllerActorItTest", ConfigFactory.parseString(SimpleControllerTest.config)))
  with FlatSpecLike
  with ImplicitSender
  with BeforeAndAfterAll
  with MockitoSugar
  with ArgumentMatchersSugar {

  import SimpleControllerTest._

  it should "correctly scrap all link from given url " in {
    given(asyncScrapingService.getUrlContent(any)(any, any)).willReturn(Future.successful(emptyHtml))
    given(asyncScrapingService.getUrlContent(eqTo(rootUrl))(any, any)).willReturn(Future.successful(rootHtml))

    val controller = simpleControllerActor(system, asyncScrapingService, configuration, webScraperConfiguration)
    val resultWebsiteData = childrenUrls
      .map(url => WebsiteData(url, None, Map.empty))
      .toSet + WebsiteData(rootUrl, Some(scrapingScenario.name), Map.empty)

    controller ! StartScraping(rootUrl)

    expectMsg(
      FetchedData(
        result = resultWebsiteData
      )
    )
  }

  private def simpleControllerActor(
    actorSystem: ActorSystem,
    asyncScrapingService: AsyncScrapingService,
    configuration: Configuration,
    webScraperConfiguration: WebScraperConfiguration
  ): ActorRef = {
    lazy val asyncHttpClient         = wire[AsyncHttpClient]
    lazy val urlRegexMatchingService = wire[UrlRegexMatchingService]
    lazy val htmlParsingService      = wire[HtmlParsingService]
    lazy val timeProvider            = wire[SimpleTimeProvider]

    lazy val scraperResolverService = wire[ScraperResolverService]
    lazy val throttlingService      = wire[ThrottlingService]
    lazy val urlPriorityService     = wire[UrlPriorityService]

    actorSystem.actorOf(
      SimpleController.props(scraperResolverService, throttlingService, urlPriorityService, configuration)
    )
  }
}

object SimpleControllerTest extends MockitoSugar {

  val config =
    """
      |prio-dispatcher {
      |  mailbox-type = "agh.petrie.scraping.actors.controllers.FrontierPriorityQueueMailbox"
      |}
      |""".stripMargin

  val asyncScrapingService = mock[AsyncScrapingService]

  val rootUrl           = "https://starturl.com"
  val maxSearchDepth    = 1
  val emptyVisitJournal = ScheduledVisitJournal.empty

  val scrapingScenario = new ScrapingScenario(
    id = None,
    name = "someName",
    preScrapingConfiguration = PreScrapingConfiguration.empty(),
    scrapingConfiguration = ScrapingConfiguration.empty(),
    postScrapingConfiguration = PostScrapingConfiguration.empty(),
    targetScenario = None
  )

  val configuration: Configuration = Configuration(
    rootScenario = List(scrapingScenario),
    urlPriorities = List.empty,
    maxSearchDepth = maxSearchDepth,
    noScenarioFallback = ScrapAll,
    scrapingType = AsyncScraping,
    isTestScraping = false
  )

  val webScraperConfiguration = WebScraperConfiguration(
    seleniumDriversCount = 2,
    asyncScraperTimeoutInSeconds = 2,
    dynamicScraperTimeoutInSeconds = 10,
    throttlingDelayTimeInSeconds = 3
  )

  val childrenUrls = List(
    "https://www.child1.com",
    "https://www.child2.com",
    "https://www.child3.com"
  )

  val rootHtmlBody =
    s"""
      | <div>
      | ${childrenUrls.zipWithIndex.map { case (url, i) => s"<a href='$url'>Page ${i + 1}</a>" }.mkString("\n")}
      | </div>
      |""".stripMargin

  val emptyHtml: Html = Html("<div></div>")
  val rootHtml: Html  = Html(body = rootHtmlBody)

  val rootWebsiteData = WebsiteData(rootUrl, Some(scrapingScenario.name), Map.empty)

  val childrenWebsiteData: List[WebsiteData] = childrenUrls
    .map(url => WebsiteData(url, None, Map.empty))
}
