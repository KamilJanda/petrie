package agh.petrie.core.repositories

import javax.inject.Singleton

import agh.petrie.core.model.infra.{RequestHistories, RequestHistoryRow}
import agh.petrie.core.model.view.FetchLinksRequest
import slick.lifted.{Compiled, Rep}
import slick.jdbc.PostgresProfile.api._
import io.scalaland.chimney.dsl._
import org.joda.time.DateTime
import slick.jdbc.PostgresProfile

import scala.concurrent.ExecutionContext

sealed trait RequestHistoryQueries {

  lazy val byId = Compiled { id: Rep[Long] =>
    for {
      historyRow <- RequestHistories.query if historyRow.id === id
    } yield historyRow
  }

  def saveRow(row: RequestHistoryRow) = RequestHistories.query += row

}

sealed class RequestHistoryDao extends RequestHistoryQueries {

  def findById(id: Long)(implicit ec: ExecutionContext): DBIO[Option[RequestHistoryRow]] =
    byId(id).result.map(_.headOption)

  def save(requestHistoryRow: RequestHistoryRow): DBIO[Int] =
    saveRow(requestHistoryRow)

}

@Singleton
class RequestHistoryRepository {

  val requestHistoryDao = new RequestHistoryDao()

  def save(fetchLinksRequest: FetchLinksRequest): DBIO[Int] =
    requestHistoryDao.save(toEntity(fetchLinksRequest))

  def toEntity(fetchLinksRequest: FetchLinksRequest) = {
    fetchLinksRequest
      .into[RequestHistoryRow]
      .withFieldConst(_.date, DateTime.now)
      .withFieldConst(_.id, None)
      .withFieldComputed(_.depth, _.configuration.maxSearchDepth)
      .transform
  }
}
