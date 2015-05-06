package shard

sealed trait AttrDiff {
  type Value
  val value: Value
}
case class OneAttrDiff[V](value: Option[V]) extends AttrDiff { type Value = Option[V] }
case class ManyAttrDiff[V](value: Map[V,Boolean]) extends AttrDiff { type Value = Map[V,Boolean] }

sealed trait EntityChange {
  val id: Long
}
case class Upserted(id: Long, diffs: Map[String,AttrDiff] = Map[String,AttrDiff]()) extends EntityChange {
  def get[A <: Attr](attr: A): Option[A#Diff#Value] = attr.castDiff(diffs.get(attr.id)).map(_.value)
  def set[A <: Attr](attr: A)(value: attr.Value, added: Boolean) =
    new Upserted(id, diffs + (attr.id -> attr.diff(get(attr), value, added)))
}
case class Removed(id: Long) extends EntityChange {
  def toJSON = id.toString
}