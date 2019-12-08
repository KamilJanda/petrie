package agh.petrie.scraping.service

import agh.petrie.scraping.model._
import agh.petrie.scraping.service.HtmlParsingService.{Html, WebsiteContent}
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchersSugar
import org.mockito.BDDMockito.given
import org.scalatest.junit.JUnitRunner
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FlatSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class HtmlParsingServiceTest extends FlatSpec with Matchers with MockitoSugar with ArgumentMatchersSugar {

  import HtmlParsingServiceTest._

  val urlRegexMatchingService = mock[UrlRegexMatchingService]
  val htmlParsingService      = new HtmlParsingService(urlRegexMatchingService)

  it should "fetch urls using topical crawling" in {
    given(urlRegexMatchingService.matchRegex(any)(any)).willReturn(true)

    val scenario = scrapingScenario(topicalScrapingConfiguration)
    val result = htmlParsingService.fetchContent(
      rootHtml,
      Right(scenario),
      isTestScraping = false
    )

    result should be(WebsiteContent(Some(scenario.name), Map.empty, topicalUrls))
  }

  it should "fetch urls using css selectors" in {
    given(urlRegexMatchingService.matchRegex(any)(any)).willReturn(true)

    val scenario = scrapingScenario(selectorScrapingConfiguration)
    val result = htmlParsingService.fetchContent(
      rootHtml,
      Right(scenario),
      isTestScraping = false
    )

    result should be(WebsiteContent(Some(scenario.name), Map.empty, selectorUrls))
  }

  it should "fetch all urls if scraping configuration is empty and FallbackScenario is not defined" in {
    given(urlRegexMatchingService.matchRegex(any)(any)).willReturn(true)

    val scenario = scrapingScenario(emptyScrapingConfiguration)
    val result = htmlParsingService.fetchContent(
      rootHtml,
      Right(scenario),
      isTestScraping = false
    )

    result.urls.toList should contain theSameElementsAs allUrls
  }

  it should "not fetch urls if scraping configuration is empty and FallbackScenario is defined" in {
    given(urlRegexMatchingService.matchRegex(any)(any)).willReturn(true)

    val result = htmlParsingService.fetchContent(
      rootHtml,
      Left(DontScrap),
      isTestScraping = false
    )

    result should be(WebsiteContent(None, Map.empty, Seq()))
  }

}

object HtmlParsingServiceTest {

  val topic    = "Cars"
  val selector = "article > div.someClass"

  val emptyScrapingConfiguration = ScrapingConfiguration(
    elementsToFetchUrlsFrom = List.empty,
    elementsToScrapContentFrom = List.empty,
    topicsToFetchUrlsFrom = List.empty
  )

  val topicalScrapingConfiguration = ScrapingConfiguration(
    elementsToFetchUrlsFrom = List.empty,
    elementsToScrapContentFrom = List.empty,
    topicsToFetchUrlsFrom = List(ScrapingTopic(topicType = KeyWord, topicSelector = topic))
  )

  val selectorScrapingConfiguration = ScrapingConfiguration(
    elementsToFetchUrlsFrom = List(SelectorConfiguration(selectorType = CssSelector, selector = selector)),
    elementsToScrapContentFrom = List.empty,
    topicsToFetchUrlsFrom = List.empty
  )

  val selectorUrls = List(
    "https://www.child1.com",
    "https://www.child2.com",
    "https://www.child3.com"
  )

  val topicalUrls = List(
    "https://www.topic1.com",
    "https://www.topic2.com",
    "https://www.topic3.com"
  )

  val allUrls: Seq[String] = topicalUrls ::: selectorUrls

  val rootHtmlBody: String =
    s"""
       | <article>
       |  <div class="someClass">
       |    ${selectorUrls.zipWithIndex.map { case (url, i) => s"<a href='$url'>Page ${i + 1}</a>" }.mkString("\n")}
       |  </div>
       | </article>
       |  <div>
       | ${topicalUrls.zipWithIndex
         .map { case (url, i)                               => s"<a href='$url'>Page about some $topic ${i + 1}</a>" }
         .mkString("\n")}
       | </div>
       |""".stripMargin

  val rootHtml: Html = Html(body = rootHtmlBody)

  def scrapingScenario(scrapingConfiguration: ScrapingConfiguration) = new ScrapingScenario(
    id = None,
    name = "someName",
    preScrapingConfiguration = PreScrapingConfiguration.empty(),
    scrapingConfiguration = scrapingConfiguration,
    postScrapingConfiguration = PostScrapingConfiguration.empty(),
    targetScenario = None
  )
}
