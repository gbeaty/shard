package shard.server

import shard._
import shard.client

import scalajs._
import datomisca._
import upickle._

trait Selector {
  def select(changes: DbChangeset): Map[Long,EntityChange]

  def apply(cs: DbChangeset) = new DbChangeset(cs.dbBefore, cs.dbAfter, select(cs))
}

case class Projector(attrs: Set[Attribute[_,_<:Cardinality]]) {
  val attrNames = attrs.map(_.ident.toString)

  def attrIds(implicit db: Database) = attrs.map(attr => db.entity(attr.ident).id)

  // def commonAttrs(entity: datomisca.Entity, id: Long) = entity.keySet.intersect
  // FIX ME: Projected entity changes don't always match their selections!

  def apply(changeset: DbChangeset) = new ServerChangeset(
    changeset.dbBefore.basisT,
    changeset.dbAfter.basisT,
    changeset.changes.toSeq.flatMap { kv =>
      val (eid, change) = kv
      (change match {
        case ins: Inserted => Some(ins)
        case up: Updated => {
          val rem = up.changes.filter(kv => attrNames.contains(kv._1))
          if(rem.size > 0)
            Some(new Updated(eid, rem))
          else
            None
        }
        case rem: Removed => Some(rem)
      }).map(eid -> _)
    }.toMap
  )
}