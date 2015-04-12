package sync.server

import sync._
import sync.client._

import datomisca._

case class Changeset(beforeDb: Database, afterDb: Database, changes: Map[FinalId,EntityChange])

object Changeset {
  def apply(reports: Seq[TxReport]): Changeset = {
    val dbBefore = reports.map(_.dbBefore).minBy(_.basisT)
    val dbAfter = reports.map(_.dbAfter).maxBy(_.basisT)
    Changeset(
      dbBefore,
      dbAfter,
      reports.map(EntityChange(_)).flatten.foldLeft(Map[FinalId,EntityChange]()) { (res,entityChange) =>
        val id = entityChange.id
        (res.get(id), entityChange) match {
          case (None, ec) => res + (id -> entityChange)
          case (Some(ins: Inserted), rem: Removed) => res
          case (Some(ins: Inserted), up: Updated) => res + (id -> ins)
          case (Some(up: Updated), upn: Updated) => up.merge(upn).map(merged => res + (id -> merged)).getOrElse(res)
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

object EntityChange {
  def entityExists(entity: Entity) = entity.keySet.size > 0

  def apply(txReport: TxReport): Seq[EntityChange] = {
    val dbBefore = txReport.dbBefore
    val dbAfter = txReport.dbAfter

    txReport.txData.groupBy(_.id).map { kv =>
      val id = new FinalId(kv._1)
      val datoms = kv._2
      val entityBefore = dbBefore.entity(id)
      val entityAfter = dbAfter.entity(id)

      if(!entityExists(entityBefore))
        Inserted(id)
      else
        if(entityExists(entityAfter))
          Updated(id, datoms.groupBy(_.attrId).map { kv =>
            val (attrId, datoms) = kv
            (attrId -> FactChange(datoms.map(datom => (datom.value -> datom.added)).toMap))
          }.toMap)
        else
          Removed(id)
    }.toSeq
  }
}