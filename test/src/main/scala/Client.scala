package sync.test

import sync._
import sync.client._

import scalajs._

/*case class ClientEntity(id: Long, data: Map[AttrId,Any]) extends Entity {
  def get[V](attr: OneAttrId[V]) = data.get(attr).map(_.asInstanceOf[V])
  def get[V](attr: ManyAttrId[V]) = data.get(attr).map(_.asInstanceOf[js.Array[V]]).getOrElse(js.Array[V]())
  
  def mod[V](changes: Map[Int,FactChange[_]]) = ClientEntity(id, changes.foldLeft(data){ (res, kv) =>
    val (attrId, factChange) = kv
    res
  })
}*/