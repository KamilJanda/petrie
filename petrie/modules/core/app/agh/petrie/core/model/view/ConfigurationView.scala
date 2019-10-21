package agh.petrie.core.model.view

import agh.petrie.scraping.model._
import play.api.libs.json.Json

case class ConfigurationView(
  scenarios: List[ScrapingScenarioView],
  maxSearchDepth: Int,
  scrapAllIfNoScenario: Boolean,
  scrapDynamically: Boolean
)

final case class ScrapingScenarioView(
  id: Option[ScrapingScenarioId],
  name: String,
  preScrapingConfiguration: PreScrapingConfigurationView,
  scrapingConfiguration: ScrapingConfigurationView,
  postScrapingConfiguration: PostScrapingConfigurationView,
  targetScenario: Option[String],
  isRootScenario: Boolean
)

final case class PreScrapingConfigurationView(
  preScrapingConfigurationElementsViews: List[PreScrapingConfigurationElementView]
)

sealed trait PreScrapingConfigurationElementView
case class ElementToClickView(selector: SelectorConfigurationView, actionType: String) extends PreScrapingConfigurationElementView
case class WaitTimeoutView(timeout: Int, actionType: String) extends PreScrapingConfigurationElementView
case class ScrollToElementView(selector: SelectorConfigurationView, actionType: String) extends PreScrapingConfigurationElementView
case class WriteToElementView(selector: SelectorConfigurationView, text: String, actionType: String) extends PreScrapingConfigurationElementView

final case class ScrapingConfigurationView(
  elementsToFetchUrlsFrom: List[SelectorConfigurationView],
  elementsToScrapContentFrom: List[SelectorConfigurationView]
)

final case class PostScrapingConfigurationView(
  urlConfiguration: List[UrlConfigurationView]
)

final case class UrlConfigurationView(
  regex: String
)

final case class SelectorConfigurationView(
  isXpathSelector: Boolean,
  selector: String
)

object ConfigurationView {
  implicit lazy val idFormat                            = Json.format[ScrapingScenarioId]
  implicit lazy val urlConfigurationViewFormat          = Json.format[UrlConfigurationView]
  implicit lazy val selectorConfigurationViewFormat     = Json.format[SelectorConfigurationView]

  import play.api.libs.json._
  import agh.petrie.common.ReadsUtils._

  private implicit class AddOps[A](writes: Writes[A]) {
    def withType(event: PreScrapingConfigurationElementView): Writes[A] =
      writes.transform(value => value.as[JsObject] ++ Json.obj("type" -> extractClassName(event)))
  }

   val preScrapingConfigurationElementViewReads: Reads[PreScrapingConfigurationElementView] = (__ \ "actionType").read[String].flatMap{
    case c if c == extractClassName(ElementToClickView) => Json.reads[ElementToClickView].asInstanceOf[Reads[PreScrapingConfigurationElementView]]
    case c if c == extractClassName(WaitTimeoutView) => Json.reads[WaitTimeoutView].asInstanceOf[Reads[PreScrapingConfigurationElementView]]
    case c if c == extractClassName(ScrollToElementView) => Json.reads[ScrollToElementView].asInstanceOf[Reads[PreScrapingConfigurationElementView]]
    case c if c == extractClassName(WriteToElementView) => Json.reads[WriteToElementView].asInstanceOf[Reads[PreScrapingConfigurationElementView]]
  }

   val preScrapingConfigurationElementViewWrites: Writes[PreScrapingConfigurationElementView] = Writes {
    case c: ElementToClickView  => Json.writes[ElementToClickView].withType(c).writes(c)
    case c: WaitTimeoutView     => Json.writes[WaitTimeoutView].withType(c).writes(c)
    case c: ScrollToElementView => Json.writes[ScrollToElementView].withType(c).writes(c)
    case c: WriteToElementView  => Json.writes[WriteToElementView].withType(c).writes(c)
    case _ => JsObject.empty
  }

  implicit val preScrapingConfigurationElementViewFormat = Format(preScrapingConfigurationElementViewReads,preScrapingConfigurationElementViewWrites)

  implicit lazy val preScrapingConfigurationViewFormat  = Json.format[PreScrapingConfigurationView]
  implicit lazy val scrapingConfigurationViewFormat     = Json.format[ScrapingConfigurationView]
  implicit lazy val postScrapingConfigurationViewFormat = Json.format[PostScrapingConfigurationView]
  implicit lazy val scenarioFormat                      = Json.format[ScrapingScenarioView]
  implicit lazy val configurationFormat                 = Json.format[ConfigurationView]
}
