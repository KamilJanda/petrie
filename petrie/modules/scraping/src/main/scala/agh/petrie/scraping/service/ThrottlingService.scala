package agh.petrie.scraping.service

import java.time.Instant

import agh.petrie.scraping.WebScraperConfiguration
import agh.petrie.scraping.service.ThrottlingService.ScheduledVisitJournal

import scala.concurrent.duration._

case object HostNotFoundInJournal extends Exception

class ThrottlingService(
  timeProvider: TimeProvider,
  webScraperConfiguration: WebScraperConfiguration
) {

  private lazy val delayTime: FiniteDuration = webScraperConfiguration.throttlingDelayTimeInSeconds second

  def getDelay(host: String, scheduledVisitJournal: ScheduledVisitJournal): Option[FiniteDuration] = {
    scheduledVisitJournal
      .getHostVisitTime(host)
      .fold {
        throw HostNotFoundInJournal
      } { time =>
        val delay = time.getEpochSecond - timeProvider.currentTimeInSecond
        if (delay > 0) Some(delay second)
        else None
      }
  }

  def updateVisitJournal(host: String, journal: ScheduledVisitJournal): ScheduledVisitJournal =
    journal.getHostVisitTime(host) match {
      case None => journal.addScheduledVisit(host, Instant.now())
      case Some(time) =>
        if (time.plusSeconds(delayTime.toSeconds).isBefore(Instant.now()))
          journal.addScheduledVisit(host, Instant.now())
        else
          journal.addScheduledVisit(host, time.plusSeconds(delayTime.toSeconds))
    }

}

object ThrottlingService {

  case class ScheduledVisitJournal(hostVisitJournal: Map[String, Instant]) {

    def addScheduledVisit(hostname: String, timestamp: Instant): ScheduledVisitJournal =
      ScheduledVisitJournal(hostVisitJournal + (hostname -> timestamp))

    def getHostVisitTime(hostname: String): Option[Instant] =
      hostVisitJournal.get(hostname)
  }

  object ScheduledVisitJournal {
    def empty = ScheduledVisitJournal(Map.empty)
  }
}

trait TimeProvider {
  def currentTimeInSecond: Long
}

class SimpleTimeProvider extends TimeProvider {
  override def currentTimeInSecond: Long = Instant.now().getEpochSecond
}
