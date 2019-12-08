package agh.petrie.scraping.actors.receptionist

import agh.petrie.scraping.actors.controllers.BaseController.StartScraping
import agh.petrie.scraping.actors.receptionist.SimpleReceptionist.{FetchedData, WebsiteData}
import agh.petrie.scraping.actors.receptionist.SimpleReceptionistTest.mock
import agh.petrie.scraping.actors.receptionist.StreamingReceptionist.GetUrls
import agh.petrie.scraping.model._
import agh.petrie.scraping.service.{ScraperResolverService, ThrottlingService, UrlPriorityService}
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchersSugar
import org.scalatest.junit.JUnitRunner
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike}

@RunWith(classOf[JUnitRunner])
class StreamingReceptionistTest
  extends TestKit(ActorSystem("StreamingReceptionistActorTest"))
  with FlatSpecLike
  with ImplicitSender
  with BeforeAndAfterAll
  with MockitoSugar
  with ArgumentMatchersSugar {

  import StreamingReceptionistTest._

  it should "fetch data from given url" in {
    val socketActor           = TestProbe()
    val controller            = TestProbe()
    val streamingReceptionist = streamingReceptionistActor(system, socketActor.ref, controller.ref)

    streamingReceptionist ! GetUrls(rootUrl, configuration)

    controller.expectMsg(StartScraping(rootUrl))

    controller.send(socketActor.ref, FetchedData(Set(rootWebsiteData)))

    socketActor.expectMsg(FetchedData(Set(rootWebsiteData)))
  }
}

object StreamingReceptionistTest {

  val scraperResolverService = mock[ScraperResolverService]
  val throttlingService      = mock[ThrottlingService]
  val urlPriorityService     = mock[UrlPriorityService]

  val rootUrl        = "www.root.org"
  val maxSearchDepth = 1

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

  val rootWebsiteData = WebsiteData(rootUrl, Some(scrapingScenario.name), Map.empty)

  def streamingReceptionistActor(actorSystem: ActorSystem, webSocketActor: ActorRef, controller: ActorRef): ActorRef =
    actorSystem.actorOf(
      Props(
        new StreamingReceptionist(scraperResolverService, throttlingService, urlPriorityService, webSocketActor) {
          override def stream(rootUrl: String, depth: Int, configuration: Configuration): Unit =
            controller ! StartScraping(rootUrl)
        }
      )
    )

}
