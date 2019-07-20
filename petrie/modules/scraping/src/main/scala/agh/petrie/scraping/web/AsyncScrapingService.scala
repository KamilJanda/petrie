package agh.petrie.scraping.web

import java.util.concurrent.Executor

import agh.petrie.scraping.service.HtmlParsingService.Html
import com.ning.http.client.{AsyncHttpClient, ListenableFuture, Response}
import agh.petrie.scraping.web.AsyncScrapingService.BadStatus
import scala.concurrent.{ExecutionContext, Future, Promise}

class AsyncScrapingService(asyncHttpClient: AsyncHttpClient) {

  def getUrlContent(url: String)(implicit exec: Executor, ec: ExecutionContext): Future[Html] = {
    val call = asyncHttpClient.prepareGet(url).execute()
    val promise = Promise[Html]
    call.addListener(getHtmlListener(promise, call), exec)
    promise.future
  }

  private def getHtmlListener(promise: Promise[Html], call: ListenableFuture[Response]) = {
    new Runnable {
      override def run = {
        try {
          val response = call.get
          if (response.getStatusCode < 400) {
            promise.success(Html(response.getResponseBody))
          } else {
            promise.failure(BadStatus(response.getStatusCode))
          }
        } catch {
          case e => println(e)
        }

      }
    }
  }
}

object AsyncScrapingService {
  case class BadStatus(code: Int) extends Exception
}
