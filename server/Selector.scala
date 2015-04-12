package sync.server

import datomisca._

trait Selector {
  def refresh(implicit db: Database): Set[Entity]
  def selectEntity(entity: Entity): Boolean

  def apply(change: EntityChange) = change match {
    case in @ Inserted(entity) => if(selectEntity(entity)) Some(in) else None
    case up @ Updated(before, after, updates) => {
      val selectedBefore = selectEntity(before)
      val selectedAfter = selectEntity(after)
      if(selectedBefore)
        if(!selectedAfter)
          Some(Removed(before))
        else
          Some(up)
      else
        if(selectedAfter)
          Some(Inserted(after))
        else
          None
    }
    case rem @ Removed(entity) => if(selectEntity(entity)) Some(rem) else None
  }
}

class AttrValSelector[DD <: AnyRef,V]
  (val attr: Attribute[DD,One], val value: V)
  (implicit val r: Attribute2EntityReaderInj[DD,One,V], val tdd: ToDatomicCast[V]) extends Selector {
    def selectEntity(entity: Entity) = entity.get(attr) == Some(value)

    final val query = Query("""[
      :find ?eid
      :in $ ?value
      :where [?eid :event/stateId ?value]
    ]""")
    def refresh(implicit db: Database) =
      Datomic.q(query, db, value).map(id => db.entity(id.asInstanceOf[Long])).toSet
}

class ReverseAttrSelector(parent: FinalId, attr: Attribute[DatomicRef.type,One]) extends Selector {
  val revAttr = attr.reverse
  def selectEntity(entity: Entity) = entity.get(attr) == Some(parent.underlying)
  def refresh(implicit db: Database) =
    db.entity(parent).read(revAttr)(Attribute2EntityReaderCast.attr2EntityReaderCastManyIdOnly).map(db.entity(_))
}