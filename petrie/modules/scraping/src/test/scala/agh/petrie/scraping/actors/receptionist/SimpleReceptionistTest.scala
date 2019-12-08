package agh.petrie.scraping.actors.receptionist

import agh.petrie.scraping.actors.controllers.BaseController.StartScraping
import agh.petrie.scraping.actors.receptionist.SimpleReceptionist.{FetchedData, GetUrls, Job, WebsiteData}
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
class SimpleReceptionistTest
  extends TestKit(ActorSystem("SimpleReceptionistActorTest"))
  with FlatSpecLike
  with ImplicitSender
  with BeforeAndAfterAll
  with MockitoSugar
  with ArgumentMatchersSugar {

  import SimpleReceptionistTest._

  it should "fetch data from given url" in {
    val controller = TestProbe()

    val simpleReceptionist = simpleReceptionistActor(system, controller.ref)

    simpleReceptionist ! GetUrls(rootUrl, configuration)

    controller.expectMsg(StartScraping(rootUrl))

    controller.send(simpleReceptionist, FetchedData(Set(rootWebsiteData)))

    expectMsg(FetchedData(Set(rootWebsiteData)))
  }

}

object SimpleReceptionistTest extends MockitoSugar {

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

  def simpleReceptionistActor(actorSystem: ActorSystem, controller: ActorRef): ActorRef =
    actorSystem.actorOf(Props(new SimpleReceptionist(scraperResolverService, throttlingService, urlPriorityService) {
      override def runNextJob(jobs: Vector[Job]): Receive = {
        if (jobs.isEmpty) {
          idle
        } else {
          val job = jobs.head
          controller ! job.action
          working(jobs)
        }
      }
    }))

}
