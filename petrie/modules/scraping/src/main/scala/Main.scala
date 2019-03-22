import akka.actor.ActorSystem
import scraping.actors.Receptionist

import scala.concurrent._
import ExecutionContext.Implicits.global
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

object Main extends ScrapingModule {
  def main(args: Array[String]): Unit = {

    implicit val timeout = Timeout(3 seconds)

    val system = ActorSystem("testSystem")
    val receptionst = system.actorOf(Receptionist.props(asyncScrapingService, htmlParsingService))

    val response = receptionst ? Receptionist.GetUrls("https://en.wikipedia.org/wiki/Main_Page", 0)
    val result = Await.result(response, timeout.duration).asInstanceOf[Receptionist.UrlsFetched]

    for {
      url <- result.urls
      _ = println(url)
    } yield ()

    system.stop(receptionst)
    asyncHttpClient.close()
    Await.result(system.terminate, timeout.duration)
  }
}