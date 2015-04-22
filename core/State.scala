package shard

trait State {
  type AttrIdent
  type Version
  type ConcreteEntity <: Entity

  sealed trait Attr {
    type Value
    type Returned
    type Diff
    val ident: AttrIdent
    def castReturn(a: Any): Returned
    def castDiff(a: Any): Diff
    def diff(orig: Returned, diff: Diff, value: Value, added: Boolean): Option[Diff]
  }
  sealed trait AttrOf[V,R,D] extends Attr {
    type Value = V
    type Returned = R
    type Diff = D
  }
  class OneAttr[V](val ident: AttrIdent) extends AttrOf[V,Option[V],Option[V]] {
    def castReturn(a: Any) = a.asInstanceOf[Option[V]]
    def castDiff(a: Any) = a.asInstanceOf[Option[V]]
    def diff(orig: Option[V], diff: Option[V], value: V, added: Boolean) =
      if(!added)
        None
      else
        if(orig.exists(_ == value)) None else Some(Some(value))
  }
  class ManyAttr[V](val ident: AttrIdent) extends AttrOf[V,Set[V],Map[V,Boolean]] {
    def castReturn(a: Any) = a.asInstanceOf[Set[V]]
    def castDiff(a: Any) = a.asInstanceOf[Map[V,Boolean]]
    def diff(orig: Set[V], diff: Map[V,Boolean], value: V, added: Boolean) = {
      val newDiff = if(added)
        if(orig.contains(value))
          diff - value
        else
          diff + (value -> true)
      else 
        if(orig.contains(value))
          diff + (value -> false)
        else
          diff - value

      if(newDiff.size == 0)
        Some(newDiff)
      else
        None
    }
  }

  trait Entity {
    def getAny(attrIdent: AttrIdent): Option[Any]
    def keys: Set[AttrIdent]
    
    def get[A <: Attr](attr: A) = attr.castReturn(getAny(attr.ident))
  }

  sealed trait EntityChange
  class Inserted(val entity: ConcreteEntity) extends EntityChange
  class Updated(val orig: ConcreteEntity, val underlying: Map[Attr,Any] = Map[Attr,Any]()) extends EntityChange {
    def get[A <: Attr](attr: A) = attr.castDiff(underlying.get(attr))
    def add[V](attr: AttrOf[V,_,_], value: V, added: Boolean) =
      attr.diff(orig.get(attr), get(attr), value, added).map { diff =>
        new Updated(orig, underlying + (attr -> diff))
      }
  }
  class Removed(val entity: ConcreteEntity) extends EntityChange  
}