package shard.server

import shard._
import shard.client._

import datomisca._

class DbChangeset(val dbBefore: Database, val dbAfter: Database, changes: Map[Long,EntityChange])
  extends ServerChangeset(dbBefore.basisT, dbAfter.basisT, changes)
object DbChangeset {
  def apply(reports: Seq[TxReport]) = {
    val dbBefore = reports.map(_.dbBefore).minBy(_.basisT)
    val dbAfter = reports.map(_.dbAfter).maxBy(_.basisT)

    /*def add(up: Updated, attr: Attr, value: Any, added: Boolean) =
      attr.diff(lookup(up.id, attr)(dbBefore), up.get(attr), value, added).map { diff =>
        new Updated(up.id, up.changes + (attr.ident.toString -> diff))
      }*/

    new DbChangeset(
      dbBefore,
      dbAfter,
      reports.map(_.txData).flatten.foldLeft(Map[Long,EntityChange]()) { (res, datom) =>
        val eid = datom.id
        def merge(up: Updated) = Attr.fromId(datom.attrId)(dbAfter) match {
          // case Some(attr) => add(up, attr, datom.value, datom.added).map(c => res + (eid -> c)).getOrElse(res - eid)
          case Some(attr) => res + (eid -> up.add(attr, datom.value, datom.added)(dbBefore))
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