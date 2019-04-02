package agh.petrie.core.model.view

import agh.petrie.scraping.api.BasicScrapingApi
import agh.petrie.scraping.api.BasicScrapingApi._
import akka.util.ByteString
import play.api.http.{ContentTypeOf, ContentTypes, Writeable}
import play.api.libs.json.{JsPath, Json, Reads, Writes}
import play.api.mvc.Codec

object WebScraperWrites {

  implicit lazy val fail = Json.format[Fail]
  implicit lazy val message = Json.format[Message]
  implicit lazy val complete = Json.format[Complete.type]
  implicit lazy val protocol = Json.format[Protocol]

  implicit val contentType: ContentTypeOf[Protocol] = ContentTypeOf(Some(ContentTypes.TEXT))

  implicit lazy val protocolWriteable: Writeable[Protocol] = Writeable(encodeProtocol)

  def encodeProtocol(p: Protocol): ByteString = p match {
    case m :Message => Codec.utf_8.encode(m.msg)
    case f :Fail => Codec.utf_8.encode(f.ex)
    case _ => Codec.utf_8.encode("done")
  }
}
