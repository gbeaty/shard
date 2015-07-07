package shard.server

import datomisca._

import shard._

object Platform extends shard.Platform {
  type RowData = Map[Col,Any]
  type DiffData = Map[Col,Any]

  def diffData(prev: RowData, next: RowData) = next.filter(kv => prev(kv._1) != kv._2)
  def updateData(row: RowData, diff: DiffData) = row ++ diff
}

case class AttributeCol[V,DD <: AnyRef]
  (attribute: Attribute[DD,Cardinality.one.type])
  (implicit attrC: Attribute2EntityReaderInj[DD,Cardinality.one.type,V]) extends shard.ColOf[V] {
    def apply(entity: Entity) = entity.get(attribute)
  }