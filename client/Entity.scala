package sync.client

import sync._

case class EntityId(id: Long) extends AnyVal

trait Entity {
  val id: EntityId

  def get[V](attr: OneAttrId[V]): Option[V]
  def get[V](attr: ManyAttrId[V]): Set[V]
}