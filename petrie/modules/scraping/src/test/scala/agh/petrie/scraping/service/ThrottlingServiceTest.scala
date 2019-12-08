package agh.petrie.scraping.service

import java.time.Instant

import agh.petrie.scraping.WebScraperConfiguration
import agh.petrie.scraping.service.ThrottlingService.ScheduledVisitJournal
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchersSugar
import org.mockito.BDDMockito.given
import org.scalatest.junit.JUnitRunner
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.duration._
import scala.language.postfixOps

@RunWith(classOf[JUnitRunner])
class ThrottlingServiceTest extends FlatSpec with Matchers with MockitoSugar with ArgumentMatchersSugar {

  import ThrottlingServiceTest._

  val timeProvider      = mock[TimeProvider]
  val configuration     = mock[WebScraperConfiguration]
  val throttlingService = new ThrottlingService(timeProvider, configuration)

  it should "return None if timestamp is in past" in {
    given(timeProvider.currentTimeInSecond).willReturn(now)
    given(configuration.throttlingDelayTimeInSeconds).willReturn(throttlingDelay)

    val visitJournal = getExampleVisitJournal(now - 5)
    val result       = throttlingService.getDelay(hostname, visitJournal)

    result should be(None)
  }

  it should "return diff between timestamp in future and now" in {
    given(timeProvider.currentTimeInSecond).willReturn(now)
    given(configuration.throttlingDelayTimeInSeconds).willReturn(throttlingDelay)

    val visitJournal = getExampleVisitJournal(now + 5)
    val result       = throttlingService.getDelay(hostname, visitJournal)

    result should be(Some(5 seconds))
  }

  it should "thrown an exception when getting delay for host not present in journal" in {
    given(timeProvider.currentTimeInSecond).willReturn(now)
    given(configuration.throttlingDelayTimeInSeconds).willReturn(throttlingDelay)

    val visitJournal = getExampleVisitJournal(now)
    intercept[HostNotFoundInJournal.type] {
      throttlingService.getDelay(hostNotInJournal, visitJournal)
    }
  }

  it should "append delay in journal for same host added in row" in {
    given(configuration.throttlingDelayTimeInSeconds).willReturn(throttlingDelay)

    val journal        = throttlingService.updateVisitJournal(hostname, ScheduledVisitJournal.empty)
    val firsRecordTime = journal.getHostVisitTime(hostname).get
    val updatedJournal = throttlingService.updateVisitJournal(hostname, journal)

    val result = updatedJournal.getHostVisitTime(hostname).get
    result should be(firsRecordTime.plusSeconds(throttlingDelay))
  }

  it should "add current time for host with calculated delay from past" in {
    given(configuration.throttlingDelayTimeInSeconds).willReturn(throttlingDelay)

    val journal        = getExampleVisitJournal(now)
    val firsRecordTime = journal.getHostVisitTime(hostname).get
    val updatedJournal = throttlingService.updateVisitJournal(hostname, journal)

    val result = updatedJournal.getHostVisitTime(hostname).get

    result shouldNot be(firsRecordTime.plusSeconds(throttlingDelay))
  }
}

object ThrottlingServiceTest {
  val url              = " https://example.org/some/rest/api"
  val hostname         = "example.org"
  val hostNotInJournal = "notInJournal.org"

  val now             = 1573949100
  val throttlingDelay = 5

  def getExampleVisitJournal(time: Long) = ScheduledVisitJournal(Map(hostname -> Instant.ofEpochSecond(time)))
}
