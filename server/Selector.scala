package sync.server

import datomisca._

trait Selector {
  def refresh(implicit db: Database): Set[Entity]
  def selectEntity(entity: Entity): Boolean

  def apply(change: EntityChange) = change match {
    case in: Inserted => if(selectEntity(in.entity)) Some(in) else None
    case up: Updated => {
      val selectedBefore = selectEntity(up.before)
      val selectedAfter = selectEntity(up.after)
      if(selectedBefore)
        if(!selectedAfter)
          Some(new Removed(up.before))
        else
          Some(up)
      else
        if(selectedAfter)
          Some(new Inserted(up.after))
        else
          None
    }
    case rem: Removed => if(selectEntity(rem.entity)) Some(rem) else None
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