package shard.server

import shard._
import datomisca._
import boopickle._

case class Projector2[C <: Platform.Cols](cols: C)(implicit
  val rowPickler: Pickler[Platform.Row[C]],
  val rowUnpickler: Unpickler[Platform.Row[C]],
  val diffPickler: Pickler[Platform.Diff[C]],
  val diffUnpickler: Unpickler[Platform.Diff[C]]
) {

  /*def project(entities: Seq[Entity]) = entities.map { entity =>
    cols.toList.map { col =>
      entity.entity.
    }
  }*/
}

trait Projector {
  // def refresh(db: Database): Refresh
  def project(changeset: DbChangeset): ServerChangeset  
}

case class AttrProjector(attrs: Set[Attribute[_,_<:Cardinality]]) extends Projector {
  val attrNames = attrs.map(_.ident.toString)

  def attrIds(implicit db: Database) = attrs.map(attr => db.entity(attr.ident).id)

  def hasAttr(db: datomisca.Database, id: Long) = db.entity(id).keySet.intersect(attrNames).size > 0

  // def refresh(db: Database) = db

  def project(changeset: DbChangeset) = new ServerChangeset(
    changeset.dbBefore.basisT,
    changeset.dbAfter.basisT,
    changeset.changes.toSeq.flatMap { kv =>
      val (eid, change) = kv
      (change match {
        case up: Upserted => {
          val res = up.diffs.filter(kv => attrNames.contains(kv._1))
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