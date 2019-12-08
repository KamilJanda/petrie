package agh.petrie.scraping.actors.controllers

import agh.petrie.scraping.actors.controllers.BaseController.{AddUrl, CheckDone, ScrapFromUrl, StartScraping}
import agh.petrie.scraping.actors.controllers.BaseControllerTest.{
  childrenUrls,
  configuration,
  rootUrl,
  scrapingScenario
}
import agh.petrie.scraping.actors.controllers.FrontierPriorityQueue.StandardPriority
import agh.petrie.scraping.actors.receptionist.SimpleReceptionist.{FetchedData, WebsiteData}
import agh.petrie.scraping.model.Configuration
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.testkit.TestProbe
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class SimpleControllerTest extends BaseControllerTest {

  import BaseControllerTest._
  import SimpleControllerTest._

  it should "send scraped website data in one batch" in {
    val resultWebsiteData = (rootWebsiteData :: childrenWebsiteData).toSet

    val frontierPriorityQueue = TestProbe()
    val rootUrlWorker         = TestProbe()
    val childrenUrlWorkers    = getChildrenUrlTestProbes()

    given(throttlingService.updateVisitJournal(any, any)).willReturn(emptyVisitJournal)
    given(throttlingService.getDelay(any, any)).willReturn(None)
    given(urlPriorityService.getPriority(any, any)).willReturn(StandardPriority)

    given(scraperResolverService.getScraper(eqTo(rootUrl), any, any, any, any)).willReturn(rootUrlWorker.ref)

    childrenUrlWorkers.foreach {
      case (url, probe) =>
        given(scraperResolverService.getScraper(eqTo(url), any, any, any, any)).willReturn(probe.ref)
    }

    val controller = controllerActor(system, configuration, frontierPriorityQueue.ref)

    controller ! StartScraping(rootUrl)
    childrenUrls.foreach(url => controller ! AddUrl(url, depth, None))

    controller ! ScrapFromUrl(rootUrl, maxSearchDepth, Some(scrapingScenario))
    childrenUrls.foreach(url => controller ! ScrapFromUrl(url, maxSearchDepth - 1, None))

    rootUrlWorker.send(controller, CheckDone(rootWebsiteData))
    childrenUrlWorkers.foreach {
      case (url, probe) =>
        probe.send(controller, CheckDone(WebsiteData(url, None, Map.empty)))
    }

    expectMsg(FetchedData(resultWebsiteData))
  }

  it should "send empty fetch data for negative depth" in {
    val frontierPriorityQueue = TestProbe()

    val controller = controllerActor(system, configurationNegativeDepth, frontierPriorityQueue.ref)

    controller ! StartScraping(rootUrl)

    expectMsg(FetchedData(Set.empty))
  }

  override def controllerActor(
    actorSystem: ActorSystem,
    configuration: Configuration,
    frontierPriorityQueueActor: ActorRef,
    webSocketActor: Option[ActorRef]
  ): ActorRef =
    actorSystem.actorOf(
      Props(new SimpleController(scraperResolverService, throttlingService, urlPriorityService, configuration) {
        override lazy val frontierPriorityQueue: ActorRef = frontierPriorityQueueActor
      })
    )
}

object SimpleControllerTest {

  val configurationNegativeDepth = configuration.copy(maxSearchDepth = -1)

  val rootWebsiteData = WebsiteData(rootUrl, Some(scrapingScenario.name), Map.empty)

  val childrenWebsiteData = childrenUrls
    .map(url => WebsiteData(url, None, Map.empty))

  def getChildrenUrlTestProbes()(implicit actorSystem: ActorSystem): Map[String, TestProbe] =
    childrenUrls
      .map(url => url -> TestProbe())
      .toMap
}
