package shard.server

import shard._

import datomisca._

object EntityChange {
  def entityExists(entity: datomisca.Entity) = entity.keySet.size > 0

  def filter(existed: Boolean, exists: Boolean, eid: Long, data: Map[String,Any] = Map[String,Any]()) =
    (existed, exists) match {
      case (false, false) => None
      case (false, true) => Some(new Upserted(eid, data))
      case (true, false) => Some(new Removed(eid))
      case (true, true) => Some(new Upserted(eid, data))
    }

  def apply(dbBefore: Database, dbAfter: Database, eid: Long) =
    filter(entityExists(dbBefore.entity(eid)), entityExists(dbAfter.entity(eid)), eid)
}