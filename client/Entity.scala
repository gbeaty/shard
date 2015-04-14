package sync.client

import sync._

trait Entity {
  val id: Long

  def get[V](attr: OneAttrId[V]): Option[V]
  def get[V](attr: ManyAttrId[V]): Set[V]
}