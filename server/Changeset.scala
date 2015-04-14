package sync.server

import sync._
import sync.client._

import datomisca._

case class Changeset(dbBefore: Database, dbAfter: Database, changes: Map[FinalId,EntityChange])

object Changeset {
  def apply(reports: Seq[TxReport]): Changeset = {
    val dbBefore = reports.map(_.dbBefore).minBy(_.basisT)
    val dbAfter = reports.map(_.dbAfter).maxBy(_.basisT)
    Changeset(
      dbBefore,
      dbAfter,
      reports.map(EntityChange(_)).flatten.foldLeft(Map[FinalId,EntityChange]()) { (res, kv) =>
        val (id, entityChange) = kv
        (res.get(id), entityChange) match {
          case (None, ec) => res + (id -> entityChange)
          case (Some(ins: Inserted), rem: Removed) => res
          case (Some(ins: Inserted), up: Updated) => res + (id -> ins)
          case (Some(up: Updated), upn: Updated) => up.merge(upn).map(merged =>
            res + (id -> merged)
          ).getOrElse(res)
          case (Some(up: Updated), rem: Removed) => res + (id -> rem)
          case (Some(rem: Removed), ins: Inserted) => res + (id -> ins)
        }
      }
    )
  }
}

case class ChangesetPoller(implicit conn: Connection) {
  private val txReportQueue = conn.txReportQueue
  def poll = Changeset(txReportQueue.drain)
}