package shard.server

import shard._
import shard.client

import scalajs._
import datomisca._
import upickle._

case class Projector(attrs: Set[Attribute[_,_<:Cardinality]]) {
  val attrNames = attrs.map(_.ident.toString)

  def attrIds(implicit db: Database) = attrs.map(attr => db.entity(attr.ident).id)

  def hasAttr(db: datomisca.Database, id: Long) = db.entity(id).keySet.intersect(attrNames).size > 0

  def apply(changeset: DbChangeset) = new ServerChangeset(
    changeset.dbBefore.basisT,
    changeset.dbAfter.basisT,
    changeset.changes.toSeq.flatMap { kv =>
      val (eid, change) = kv
      (change match {
        case up: Upserted => {
          val res = up.changes.filter(kv => attrNames.contains(kv._1))
          if(res.size == 0)
            None
          else {
            EntityChange.filter(hasAttr(changeset.dbBefore, eid), hasAttr(changeset.dbAfter, eid), eid, res)
          }
        }
        case rem: Removed =>
          if(hasAttr(changeset.dbBefore, eid))
            Some(rem)
          else
            None
      }).map(eid -> _)
    }.toMap
  )
}