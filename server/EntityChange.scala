package shard.server

import shard._
import shard.client._

import datomisca._

object EntityChange {
  def entityExists(entity: datomisca.Entity) = entity.keySet.size > 0

  def apply(dbBefore: Database, dbAfter: Database, eid: Long) = {
    val entityBefore = dbBefore.entity(eid)
    val entityAfter = dbAfter.entity(eid)
    (entityExists(entityBefore), entityExists(entityAfter)) match {
      case (false, false) => None
      case (false, true) => Some(new Inserted(entityAfter))
      case (true, false) => Some(new Removed(entityBefore))
      case (true, true) => Some(new Updated(entityAfter))
    }
  }
}