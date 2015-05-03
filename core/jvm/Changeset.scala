package shard.server

import shard._

import datomisca._

class DbChangeset(val dbBefore: Database, val dbAfter: Database, changes: Map[Long,EntityChange])
  extends ServerChangeset(dbBefore.basisT, dbAfter.basisT, changes)
object DbChangeset {
  def apply(reports: Seq[TxReport]) = {
    val dbBefore = reports.map(_.dbBefore).minBy(_.basisT)
    val dbAfter = reports.map(_.dbAfter).maxBy(_.basisT)

    new DbChangeset(
      dbBefore,
      dbAfter,
      reports.map(_.txData).flatten.foldLeft(Map[Long,EntityChange]()) { (res, datom) =>
        val eid = datom.id
        def merge(up: Upserted) = Attr.fromId(datom.attrId)(dbAfter) match {
          case Some(attr) => res + (eid -> up.add(attr)(datom.value.asInstanceOf[attr.Value], datom.added)(dbBefore))
          // FIX ME, log exceptions.
        }
        res.get(eid) match {
          case Some(ec: Upserted) => merge(ec)
          case Some(_: Removed) => res
          case None => EntityChange(dbBefore, dbAfter, eid) match {
            case Some(ec: Upserted) => merge(ec)
            case Some(ec: Removed) => res + (eid -> ec)
            case None => res
          }
        }
      }
    )
  }
}