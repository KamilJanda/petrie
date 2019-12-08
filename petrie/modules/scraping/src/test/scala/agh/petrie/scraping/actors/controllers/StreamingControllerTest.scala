package agh.petrie.scraping.actors.controllers

import agh.petrie.scraping.actors.controllers.BaseController.{AddUrl, CheckDone, ScrapFromUrl, StartScraping}
import agh.petrie.scraping.actors.controllers.BaseControllerTest.{
  childrenUrls,
  configuration,
  rootUrl,
  scrapingScenario
}
import agh.petrie.scraping.actors.controllers.FrontierPriorityQueue.StandardPriority
import agh.petrie.scraping.actors.receptionist.SimpleReceptionist.WebsiteData
import agh.petrie.scraping.api.BasicScrapingApi.{Complete, Message}
import agh.petrie.scraping.model.Configuration
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.testkit.TestProbe
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class StreamingControllerTest extends BaseControllerTest {

  import BaseControllerTest._
  import StreamingControllerTest._

  it should "send stream of messages with scraped data" in {

    val resultWebsiteData = rootWebsiteData :: childrenWebsiteData

    val rootWorker            = TestProbe()
    val frontierPriorityQueue = TestProbe()
    val webSocketActor        = TestProbe()
    val childrenUrlTestProbes = getChildrenUrlTestProbes

    given(throttlingService.updateVisitJournal(any, any)).willReturn(emptyVisitJournal)
    given(throttlingService.getDelay(any, any)).willReturn(None)
    given(urlPriorityService.getPriority(any, any)).willReturn(StandardPriority)

    given(scraperResolverService.getScraper(eqTo(rootUrl), any, any, any, any)).willReturn(rootWorker.ref)

    childrenUrlTestProbes.foreach {
      case (url, probe) =>
        given(scraperResolverService.getScraper(eqTo(url), any, any, any, any)).willReturn(probe.ref)
    }

    val controller = controllerActor(system, configuration, frontierPriorityQueue.ref, Some(webSocketActor.ref))

    controller ! StartScraping(rootUrl)
    childrenUrls.foreach(url => controller ! AddUrl(url, depth, None))

    controller ! ScrapFromUrl(rootUrl, maxSearchDepth, Some(scrapingScenario))
    childrenUrls.foreach(url => controller ! ScrapFromUrl(url, maxSearchDepth - 1, None))

    rootWorker.send(controller, CheckDone(rootWebsiteData))
    childrenUrlTestProbes.foreach {
      case (url, probe) =>
        probe.send(controller, CheckDone(WebsiteData(url, None, Map.empty)))
    }

    webSocketActor.expectMsgAllOf(
      resultWebsiteData.map(Message): _*
    )
    webSocketActor.expectMsg(Complete)
  }

  it should "stop fetching data for negative depth" in {
    val frontierPriorityQueue = TestProbe()
    val webSocketActor        = TestProbe()

    val controller =
      controllerActor(system, configurationNegativeDepth, frontierPriorityQueue.ref, Some(webSocketActor.ref))

    controller ! StartScraping(rootUrl)

    webSocketActor.expectMsg(Complete)
  }

  override def controllerActor(
    actorSystem: ActorSystem,
    configuration: Configuration,
    frontierPriorityQueueActor: ActorRef,
    webSocketActor: Option[ActorRef]
  ): ActorRef =
    actorSystem.actorOf(
      Props(
        new StreamingController(
          scraperResolverService,
          throttlingService,
          urlPriorityService,
          configuration,
          webSocketActor.getOrElse(mock[ActorRef])
        ) {
          override lazy val frontierPriorityQueue: ActorRef = frontierPriorityQueueActor
        }
      )
    )
}

object StreamingControllerTest {

  val configurationNegativeDepth = configuration.copy(maxSearchDepth = -1)

  val rootWebsiteData = WebsiteData(rootUrl, Some(scrapingScenario.name), Map.empty)

  val childrenWebsiteData: List[WebsiteData] = childrenUrls
    .map(url => WebsiteData(url, None, Map.empty))

  def getChildrenUrlTestProbes(implicit actorSystem: ActorSystem): Map[String, TestProbe] =
    childrenUrls
      .map(url => url -> TestProbe())
      .toMap

}
