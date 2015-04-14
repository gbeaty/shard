package sync.server

import datomisca._

trait Filter extends Selector {  
  def filter(dbBefore: Database, dbAfter: Database, id: FinalId, change: EntityChange): Option[EntityChange]

  def select(changeset: Changeset) = changeset.changes.flatMap { kv =>
    val (id, change) = kv
    filter(changeset.dbBefore, changeset.dbAfter, id, change).map(id -> _)
  }.toMap
}

trait EntityFilter extends Filter {
  def refresh(implicit db: Database): Set[Entity]
  def selectEntity(entity: Entity): Boolean

  def filter(dbBefore: Database, dbAfter: Database, id: FinalId, change: EntityChange) = change match {
    case in: Inserted => if(selectEntity(dbAfter.entity(id))) Some(in) else None
    case up: Updated => {
      val entityBefore = dbBefore.entity(id)
      val entityAfter = dbAfter.entity(id)
      val selectedBefore = selectEntity(entityBefore)
      val selectedAfter = selectEntity(entityAfter)
      if(selectedBefore)
        if(!selectedAfter)
          Some(new Removed())
        else
          Some(up)
      else
        if(selectedAfter)
          Some(new Inserted(entityAfter))
        else
          None
    }
    case rem: Removed => if(selectEntity(dbBefore.entity(id))) Some(rem) else None
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

class ReverseAttrEntityFilter(parent: FinalId, attr: Attribute[DatomicRef.type,One]) extends EntityFilter {
  val revAttr = attr.reverse
  def selectEntity(entity: Entity) = entity.get(attr) == Some(parent.underlying)
  def refresh(implicit db: Database) =
    db.entity(parent).read(revAttr)(Attribute2EntityReaderCast.attr2EntityReaderCastManyIdOnly).map(db.entity(_))
}