package sync.server

import sync._
import sync.client._

import datomisca._

case class Changeset(beforeDb: Database, afterDb: Database, changes: Map[Entity,EntityChange])

object Changeset {
  def apply(reports: Seq[TxReport]): Changeset = {
    val dbBefore = reports.map(_.dbBefore).minBy(_.basisT)
    val dbAfter = reports.map(_.dbAfter).maxBy(_.basisT)
    Changeset(
      dbBefore,
      dbAfter,
      reports.map(EntityChange(_)).flatten.foldLeft(Map[Entity,EntityChange]()) { (res,entityChange) =>
        val entity = entityChange.entity
        (res.get(entity), entityChange) match {
          case (None, ec) => res + (entity -> entityChange)
          case (Some(ins: Inserted), rem: Removed) => res
          case (Some(ins: Inserted), up: Updated) => res + (entity -> ins)
          case (Some(up: Updated), upn: Updated) => up.merge(upn).map(merged => res + (entity -> merged)).getOrElse(res)
          case (Some(up: Updated), rem: Removed) => res + (entity -> rem)
          case (Some(rem: Removed), ins: Inserted) => res + (entity -> ins)
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
      val (id, datoms) = kv
      val entityBefore = dbBefore.entity(id)
      val entityAfter = dbAfter.entity(id)

      if(!entityExists(entityBefore))
        Inserted(entityAfter)
      else
        if(entityExists(entityAfter))
          Updated(entityBefore, entityAfter, datoms.groupBy(_.attrId).map { kv =>
            val (attrId, datoms) = kv
            (attrId -> FactChange(datoms.map(datom => (datom.value -> datom.added)).toMap))
          }.toMap)
        else
          Removed(entityBefore)
    }.toSeq
  }
}