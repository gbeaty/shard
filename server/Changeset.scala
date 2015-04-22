package shard.server

import shard._
import shard.client._

import datomisca._

class Changeset(before: Database, after: Database, changes: Map[Long,EntityChange])

object Changeset {
  def apply(reports: Seq[TxReport]): Changeset = {
    val dbBefore = reports.map(_.dbBefore).minBy(_.basisT)
    val dbAfter = reports.map(_.dbAfter).maxBy(_.basisT)
    new Changeset(
      dbBefore,
      dbAfter,
      reports.map(_.txData).flatten.foldLeft(Map[Long,EntityChange]()) { (res, datom) =>
        val eid = datom.id
        def merge(up: Updated) = Attr.fromId(datom.attrId)(dbAfter) match {
          case Some(attr) => up.add(attr, datom.value, datom.added).map(c => res + (eid -> c)).getOrElse(res - eid)
        }
        res.get(eid) match {
          case Some(_: Inserted) => res
          case Some(ec: Updated) => merge(ec)
          case Some(_: Removed) => res
          case None => EntityChange(dbBefore, dbAfter, eid) match {
            case Some(ec: Inserted) => res + (eid -> ec)
            case Some(ec: Updated) => merge(ec)
            case Some(ec: Removed) => res + (eid -> ec)
            case None => res
          }
        }
      }
    )
  }
}