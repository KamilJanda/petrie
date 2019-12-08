package agh.petrie.scraping.actors.controllers

import agh.petrie.scraping.actors.controllers.BaseController.{AddUrl, StartScraping}
import agh.petrie.scraping.actors.controllers.FrontierPriorityQueue.{AddUrlToQueue, StandardPriority, UrlNode}
import agh.petrie.scraping.model._
import agh.petrie.scraping.service.ThrottlingService.ScheduledVisitJournal
import agh.petrie.scraping.service.{ScraperResolverService, ThrottlingService, UrlPriorityService}
import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import org.mockito.ArgumentMatchersSugar
import org.mockito.BDDMockito.given
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike}

import scala.concurrent.duration._
import scala.language.postfixOps

abstract class BaseControllerTest
  extends TestKit(ActorSystem("BaseControllerActorTest"))
  with FlatSpecLike
  with ImplicitSender
  with BeforeAndAfterAll
  with MockitoSugar
  with ArgumentMatchersSugar {

  import BaseControllerTest._

  override def afterAll: Unit =
    TestKit.shutdownActorSystem(system)

  def controllerActor(
    actorSystem: ActorSystem,
    configuration: Configuration,
    frontierPriorityQueueActor: ActorRef,
    webSocketActor: Option[ActorRef] = None
  ): ActorRef

  it should "add to queue url without delay" in {
    given(throttlingService.updateVisitJournal(any, any)).willReturn(emptyVisitJournal)
    given(throttlingService.getDelay(any, any)).willReturn(None)
    given(urlPriorityService.getPriority(any, any)).willReturn(StandardPriority)

    val frontierPriorityQueueProbe = TestProbe()

    val controller = controllerActor(system, configuration, frontierPriorityQueueProbe.ref)

    controller ! StartScraping(rootUrl)
    controller ! AddUrl(url, depth, Some(scrapingScenario))

    frontierPriorityQueueProbe.expectMsgAllOf(
      AddUrlToQueue(UrlNode(rootUrl, maxSearchDepth, Some(scrapingScenario)), StandardPriority),
      AddUrlToQueue(UrlNode(url, depth, Some(scrapingScenario)), StandardPriority)
    )
  }

  it should "add to queue url with delay" in {
    val delay = 1 second

    given(throttlingService.updateVisitJournal(any, any)).willReturn(emptyVisitJournal)
    given(throttlingService.getDelay(any, any)).willReturn(Some(delay))
    given(urlPriorityService.getPriority(any, any)).willReturn(StandardPriority)

    val frontierPriorityQueueProbe = TestProbe()

    val controller = controllerActor(system, configuration, frontierPriorityQueueProbe.ref)

    controller ! StartScraping(rootUrl)

    frontierPriorityQueueProbe.expectNoMessage(delay)
    frontierPriorityQueueProbe.expectMsg(
      AddUrlToQueue(UrlNode(rootUrl, maxSearchDepth, Some(scrapingScenario)), StandardPriority)
    )
  }

}

object BaseControllerTest extends MockitoSugar {
  val scraperResolverService: ScraperResolverService = mock[ScraperResolverService]
  val throttlingService: ThrottlingService           = mock[ThrottlingService]
  val urlPriorityService: UrlPriorityService         = mock[UrlPriorityService]

  val rootUrl           = "https://starturl.com"
  val url               = "https://foo.org"
  val maxSearchDepth    = 2
  val depth             = 1
  val emptyVisitJournal = ScheduledVisitJournal.empty

  val childrenUrls = List(
    "https://www.child1.com",
    "https://www.child2.com",
    "https://www.child3.com"
  )

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
}
