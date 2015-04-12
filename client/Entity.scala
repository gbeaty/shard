package sync.client

import sync._

case class EntityId(id: Long) extends AnyVal

trait EntityData[S <: EntityData[S,O,M],O[_],M[_]] {
  val underlying: Map[AttrId,Any]
  def constMany[V]: M[V]
  protected def const(underlying: Map[AttrId,Any]): S
  // def assertMany[V](value: V, to: M[V]): M[V]
  // def retractMany[V](value: V, from: M[V]): M[V]

  def get[V](attr: OneAttrId[V]) = underlying.get(attr).map(_.asInstanceOf[O[V]])
  def get[V](attr: ManyAttrId[V]) = underlying.get(attr).map(_.asInstanceOf[M[V]]).getOrElse(constMany[V])
}

case class EntityFacts(underlying: Map[AttrId,Any]) extends EntityData[EntityFacts,({type S[V]=V})#S,Set] {
  def constMany[V] = Set[V]()
  protected def const(map: Map[AttrId,Any]) = EntityFacts(map)
}

case class EntityChanges(underlying: Map[AttrId,Any])
  extends EntityData[EntityChanges,FactChange,({type M[V]=Map[V,Boolean]})#M] {
    def constMany[V] = Map[V,Boolean]()
    protected def const(map: Map[AttrId,Any]) = EntityChanges(map)
  }