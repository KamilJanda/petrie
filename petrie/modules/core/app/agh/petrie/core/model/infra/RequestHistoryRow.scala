package agh.petrie.core.model.infra

import org.joda.time.DateTime
import scala.concurrent.{Future, Await}
import scala.concurrent.duration.Duration
import slick.jdbc.PostgresProfile.api._
import java.sql.Date
import scala.reflect.ClassTag
import slick.jdbc.PostgresProfile.api._
import com.github.tototoshi.slick.PostgresJodaSupport._

case class RequestHistoryRow(
  id:     Option[Long],
  date:   DateTime,
  url:    String,
  depth:  Int
)

class RequestHistories(tag: Tag) extends Table[RequestHistoryRow](tag, "request_history"){
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def date = column[DateTime]("date")
  def url = column[String]("url")
  def depth = column[Int]("depth")

  def * = (id.?, date, url, depth) <> (RequestHistoryRow.tupled, RequestHistoryRow.unapply)
}

object RequestHistories {
  val query = TableQuery[RequestHistories]
}