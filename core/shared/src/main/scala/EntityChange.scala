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
case class Upserted(id: Long, diffs: Map[String,AttrDiff]) extends EntityChange {
  def get[A <: Attr](attr: A) = attr.castDiff(diffs.get(attr.id)).map(_.value)
  def add[A <: Attr,V](attr: A{type Value=V}, value: V, added: Boolean)(implicit s: State) =
    attr.diff(s.lookup(id, attr), get(attr), value, added).map { diff =>
      new Upserted(id, diffs + (attr.id -> diff))
    }.getOrElse(this)
}
case class Removed(id: Long) extends EntityChange {
  def toJSON = id.toString
}