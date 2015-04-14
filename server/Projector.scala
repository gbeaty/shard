package sync.server

import sync._

import datomisca._
import upickle._

trait Selector {
  def select(changes: Changeset): Map[FinalId,EntityChange]

  def apply(changeset: Changeset) = changeset.copy(changes = select(changeset))
}

/*case class AttrFilter(attrs: Set[Attribute[_,_<:Cardinality]]) extends Filter {
  def apply(change: EntityChange) = change match {
    case Inserted(id, entity) =>
    case Updated(id, changes) =>
    case Removed(id) =>
  }
}*/

case class Projector(attrs: Set[Attribute[_,_<:Cardinality]]) {
  def attrIds(implicit db: Database) = attrs.map(attr => db.entity(attr.ident).id)

  def apply(changeset: Changeset) = client.Changeset(
    changeset.dbBefore.basisT,
    changeset.dbAfter.basisT,
    changeset.changes.flatMap { kv =>
      val (id, change) = kv
      (change match {
        case Inserted(entity) => Some(new client.Removed())
        case Updated(factChanges) => Some(new client.Removed())
        case Removed() => Some(new client.Removed())
      }).map(id.underlying -> _)
    }.toMap
  )
}

/*object JSONProjector extends Projector[String] {
  def apply(cs: server.Changeset) = 
}*/