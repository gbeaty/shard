package shard

trait State {
  type AttrIdent
  type Version
  type ConcreteEntity <: Entity

  // class Changeset(before: Version, after: Version, )

  sealed trait Attr {
    type Value
    type Diff
    val ident: AttrIdent
    def castReturn(a: Any): Value
    def castDiff(a: Any): Diff
    def update(value: Value, diff: Diff): Value
    def updateDiff(orig: Value, last: Diff, next: Diff): Option[Diff]
  }
  sealed trait AttrOf[R,D] extends Attr {
    type Value = R
    type Diff = D
  }
  class OneAttr[V](val ident: AttrIdent) extends AttrOf[Option[V],Option[V]] {
    def castReturn(a: Any) = a.asInstanceOf[Option[V]]
    def castDiff(a: Any) = a.asInstanceOf[Option[V]]
    def update(value: Option[V], diff: Option[V]) = diff
    def updateDiff(orig: Option[V], last: Option[V], next: Option[V]) =
      if(orig == next) None else Some(next)
  }
  class ManyAttr[V](val ident: AttrIdent) extends AttrOf[Set[V],Map[V,Boolean]] {
    def castReturn(a: Any) = a.asInstanceOf[Set[V]]
    def castDiff(a: Any) = a.asInstanceOf[Map[V,Boolean]]
    def update(values: Set[V], diff: Map[V,Boolean]) = diff.foldLeft(values) { (res, kv) =>
      val (value, assert) = kv
      if(assert)
        res + value
      else
        res - value
    }
    def updateDiff(orig: Set[V], last: Map[V,Boolean], next: Map[V,Boolean]) = {
      val res = next.foldLeft(last) { (res,kv) =>
        val (value, added) = kv
        val origHad = orig.contains(value)
        if(added && origHad || !added && !origHad)
          res - value
        else last.get(value).map(had =>
          if(had == added)
            res
          else
            res - value
        ).getOrElse(res + kv)
      }
      if(res.size == 0)
        None
      else
        Some(res)
    }
  }

  trait Entity {
    def getAny(attrIdent: AttrIdent): Option[Any]
    def keys: Set[AttrIdent]
    
    def get[A <: Attr](attr: A) = attr.castReturn(getAny(attr.ident))
  }

  class EntityDiff(val orig: ConcreteEntity, val underlying: Map[Attr,Any] = Map[Attr,Any]()) {
    def get[A <: Attr](attr: A) = attr.castDiff(underlying.get(attr))

    def merge(last: EntityDiff) = new EntityDiff(orig, underlying.keySet.foldLeft(last.underlying) { (res, attr) =>
      attr.updateDiff(orig.get(attr), last.get(attr), get(attr)).map(u => res + (attr -> u)).getOrElse(res)
    })
  }

  sealed trait EntityChange {
    def merge(next: EntityChange): Option[EntityChange]
  }
  class Inserted(val entity: ConcreteEntity) extends EntityChange {
     def merge(next: EntityChange) = next match {
      case ins: Inserted => Some(ins)
      case up:  Updated  => Some(this)
      case rem: Removed  => None
    }
  }
  class Updated(val diff: EntityDiff) extends EntityChange {
    def merge(next: EntityChange) = next match {
      case ins: Inserted => Some(ins)
      case up:  Updated  => Some(new Updated(diff.merge(up.diff)))
      case rem: Removed  => Some(rem)
    }
  }
  class Removed(val entity: ConcreteEntity) extends EntityChange {
    def merge(next: EntityChange) = next match {
      case ins: Inserted => Some(ins)
      case up:  Updated  => Some(this)
      case rem: Removed  => None
    }
  }

  class EntityChanges(underlying: Map[Long,EntityChange]) {
    def add(id: Long, change: EntityChange) =
      new EntityChanges(underlying.get(id).map(_.merge(change)).getOrElse(Some(change)).map {
        c => underlying + (id -> c)
      }.getOrElse(underlying - id))
  }

  class Changeset(before: Version, after: Version, changes: EntityChanges)
}