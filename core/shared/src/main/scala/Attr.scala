package shard

import boopickle._

sealed trait Attr {
  type Value
  type Returned
  type Diff <: AttrDiff
  val id: String
  val pickler: Pickler[Value]
  def castReturn(a: Any): Returned
  def castDiff(ad: Option[AttrDiff]): Option[Diff]
  def diff(diff: Option[Diff#Value], value: Value, added: Boolean): Diff
}
sealed trait AttrOf[V] extends Attr {
  type Value = V
}
class OneAttr[V](val id: String)(implicit val pickler: Pickler[V]) extends AttrOf[V] {
  type Returned = Option[V]
  type Diff = OneAttrDiff[V]
  def castReturn(a: Any) = a.asInstanceOf[Option[V]]
  def castDiff(ad: Option[AttrDiff]) = ad.map(_.asInstanceOf[OneAttrDiff[V]])
  def diff(diff: Option[Option[V]], value: V, added: Boolean) =
    if(!added) OneAttrDiff[V](None) else OneAttrDiff(Some(value))
}
class ManyAttr[V](val id: String)(implicit val pickler: Pickler[V]) extends AttrOf[V] {
  type Returned = Set[V]
  type Diff = ManyAttrDiff[V]
  def castReturn(a: Any) = a.asInstanceOf[Set[V]]
  def castDiff(ad: Option[AttrDiff]) = ad.map(_.asInstanceOf[ManyAttrDiff[V]])
  def diff(diff: Option[Map[V,Boolean]], value: V, added: Boolean) =
    ManyAttrDiff(diff.getOrElse(Map[V,Boolean]()) + (value -> added))
}