package scraping.web

import java.util.concurrent.Executor

import com.ning.http.client.AsyncHttpClient
import scraping.web.AsyncScrapingService.BadStatus

import scala.concurrent.{ExecutionContext, Future, Promise}

class AsyncScrapingService(asyncHttpClient: AsyncHttpClient) {

  def getUrlContent(url: String)(implicit  exec: Executor, ec: ExecutionContext): Future[String] = {
    val call = asyncHttpClient.prepareGet(url).execute()
    val promise = Promise[String]
    call.addListener(new Runnable {
      override def run = {
      val response = call.get
      if(response.getStatusCode < 400) promise.success(response.getResponseBody)
      else promise.failure(BadStatus(response.getStatusCode))
    }}, exec)
    promise.future
  }
}

object AsyncScrapingService {
  case class BadStatus(code: Int) extends Exception
}
