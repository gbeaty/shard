package shard.server

import datomisca._

object Platform extends shard.Platform {
  trait Col {
    type Value
  }

  case class Row[C <: CList](fields: Map[Col,Any])
  case class Diff[C <: CList](diffs: Map[Col,Any])

  def diff[C <: CList](last: Row[C], next: Row[C]) = Diff[C](next.fields.filter(kv => last.fields(kv._1) != kv._2))
  def update[C <: CList](row: Row[C], diff: Diff[C]) = Row[C](row.fields ++ diff.diffs)
}

case class AttributeCol[V,DD <: AnyRef]
  (attribute: Attribute[DD,Cardinality.one.type])
  (implicit attrC: Attribute2EntityReaderInj[DD,Cardinality.one.type,V]) extends Platform.Col {
    type Value = V
    def apply(entity: Entity) = entity.get(attribute)
  }