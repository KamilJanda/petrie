package agh.petrie.scraping.service

import agh.petrie.scraping.actors.controllers.FrontierPriorityQueue.{HighPriority, LowPriority, StandardPriority}
import agh.petrie.scraping.model.UrlPriority
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchersSugar
import org.scalatest.junit.JUnitRunner
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FlatSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class UrlPriorityServiceTest extends FlatSpec with Matchers with MockitoSugar with ArgumentMatchersSugar {

  import UrlPriorityServiceTest._

  it should "return StandardPriority if urlPriories list is empty" in {
    val result = new UrlPriorityService().getPriority(url, List.empty)

    result should be(StandardPriority)
  }

  it should "return StandardPriority for url not present in urlPriorities list" in {
    val result = new UrlPriorityService().getPriority(urlNotInUrlPrioritiesList, urlPriorities)

    result should be(StandardPriority)
  }

  it should "get correct priority for url present in urlPriorities list" in {
    val result = new UrlPriorityService().getPriority(url, urlPriorities)

    result should be(HighPriority)
  }

}

object UrlPriorityServiceTest {

  val url                       = "www.example.org"
  val urlNotInUrlPrioritiesList = "www.bad.org"

  val urlPriorities = List(
    UrlPriority("example.org", HighPriority),
    UrlPriority("other.org", LowPriority)
  )

}
