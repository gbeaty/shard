package shard.server

import shard._
import datomisca._

case class Selector(id: String, bindings: Binding*) {
  def selectEntity(entity: datomisca.Entity) = bindings.forall(_.apply(entity))

  def select(changeset: DbChangeset): Map[Long,EntityChange] = changeset.changes.flatMap { kv =>
    val (eid, change) = kv
    val before = selectEntity(changeset.dbBefore.entity(eid))
    (change match {
      case up: Upserted => EntityChange.filter(
        before,
        selectEntity(changeset.dbAfter.entity(eid)),
        eid,
        up.diffs
      )
      case rem: Removed => if(before) Some(rem) else None
    }).map((eid, _))
  }
}