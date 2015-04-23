package shard.server

import shard._
import datomisca._

trait Selector {
  def select(changes: DbChangeset): Map[Long,EntityChange]

  def apply(cs: DbChangeset) = new DbChangeset(cs.dbBefore, cs.dbAfter, select(cs))
}

trait Filter extends Selector {  
  def filter(dbBefore: Database, dbAfter: Database, id: Long, change: EntityChange): Option[EntityChange]

  def select(changeset: DbChangeset) = changeset.changes.flatMap { kv =>
    val (id, change) = kv
    filter(changeset.dbBefore, changeset.dbAfter, id, change).map(id -> _)
  }.toMap
}

trait EntityFilter extends Filter {
  def refresh(implicit db: Database): Set[Entity]
  def selectEntity(entity: Entity): Boolean

  def filter(dbBefore: Database, dbAfter: Database, eid: Long, change: EntityChange) = change match {
    case up: Upserted =>
      EntityChange.filter(selectEntity(dbBefore.entity(eid)), selectEntity(dbAfter.entity(eid)), eid, up.changes)
    case rem: Removed => if(selectEntity(dbBefore.entity(eid))) Some(rem) else None
  }
}

class AttrValEntityFilter[DD <: AnyRef,V]
  (val attr: Attribute[DD,One], val value: V)
  (implicit val r: Attribute2EntityReaderInj[DD,One,V], val tdd: ToDatomicCast[V]) extends EntityFilter {
    def selectEntity(entity: Entity) = entity.get(attr) == Some(value)

    final val query = Query("""[
      :find ?eid
      :in $ ?value
      :where [?eid :event/stateId ?value]
    ]""")
    def refresh(implicit db: Database) =
      Datomic.q(query, db, value).map(id => db.entity(id.asInstanceOf[Long])).toSet
}

class ReverseAttrEntityFilter(parent: Long, attr: Attribute[DatomicRef.type,One]) extends EntityFilter {
  val revAttr = attr.reverse
  def selectEntity(entity: Entity) = entity.get(attr) == Some(parent.underlying)
  def refresh(implicit db: Database) =
    db.entity(parent).read(revAttr)(Attribute2EntityReaderCast.attr2EntityReaderCastManyIdOnly).map(db.entity(_))
}