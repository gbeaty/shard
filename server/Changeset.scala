package shard.server

import shard._
import shard.client._

import datomisca._

/*object Changeset {
  def apply(reports: Seq[TxReport]) = {
    val dbBefore = reports.map(_.dbBefore).minBy(_.basisT)
    val dbAfter = reports.map(_.dbAfter).maxBy(_.basisT)
    new Changeset(
      dbBefore,
      dbAfter,
      reports.map(_.txData).flatten.groupBy(_.id)
  }
}*/

object Changeset {
  def apply(reports: Seq[TxReport]): Changeset = {
    val dbBefore = reports.map(_.dbBefore).minBy(_.basisT)
    val dbAfter = reports.map(_.dbAfter).maxBy(_.basisT)
    new Changeset(
      dbBefore,
      dbAfter,
      new EntityChanges(reports.map(_.txData).flatten.groupBy(_.id).foldLeft(Map[Long,EntityChange]()) { (res, kv) =>
        val (id, datoms) = kv
        val beforeEntity = dbBefore.entity(id)
        val afterEntity = dbAfter.entity(id)
        if(beforeEntity.keySet.size == 0)
          res + (id -> new Inserted(Entity(afterEntity)))
        else if(afterEntity.keySet.size == 0)
          res - id
        else {
          datoms.groupBy(_.attrId).map { kv =>
          // datoms.groupBy(_.attrId).foldLeft(res) { (res, kv) =>
            val (attrId, datoms) = kv
            val attr = dbAfter.entity(attrId)
            val ident = attr.getAs[Keyword](Attribute.ident)
            if(attr.getAs[String](Attribute.cardinality) == "db.cardinality/one") {
              // assert/retract single value
              val datom = datoms.last
              if(!datom.added) {
                res
              } else {
                res
              }
            } else {
              // assert retract multiple values
              res
            }
          }
        }
      })
      /*reports.map(_.txData).flatten.foldLeft(Map[Long,EntityChange]()) { (res, datom) =>
        val id = datom.id
        val entityChange: EntityChange = 1
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
      }: Map[Long,EntityChange]*/
    )
  }
}

/*case class Changeset(dbBefore: Database, dbAfter: Database, changes: Map[FinalId,EntityChange])

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
}*/