package scraping.web

import java.util.concurrent.Executor
import com.ning.http.client.{AsyncHttpClient, ListenableFuture, Response}
import scraping.web.AsyncScrapingService.{BadStatus, Html}
import scala.concurrent.{ExecutionContext, Future, Promise}

class AsyncScrapingService(asyncHttpClient: AsyncHttpClient) {

  def getUrlContent(url: String)(implicit  exec: Executor, ec: ExecutionContext): Future[Html] = {
    val call = asyncHttpClient.prepareGet(url).execute()
    val promise = Promise[Html]
    call.addListener(getHtmlListener(promise, call), exec)
    promise.future
  }

  private def getHtmlListener(promise: Promise[Html], call: ListenableFuture[Response]) = {
    new Runnable {
      override def run = {
        val response = call.get
        if(response.getStatusCode < 400) {
          promise.success(Html(response.getResponseBody))
        }
        else {
          promise.failure(BadStatus(response.getStatusCode))
        }
      }
    }
  }
}

object AsyncScrapingService {
  case class BadStatus(code: Int) extends Exception
  case class Html(body: String)
}
