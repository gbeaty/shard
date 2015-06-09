package shard

import boopickle._

sealed trait Attr {
  type Value
  type Diff <: AttrDiff
  type Values
  val id: String
  def castDiff(ad: Option[AttrDiff]): Option[Diff]
  def diff(diff: Option[Diff#Value], value: Value, added: Boolean): Diff
  def castValues(v: Option[Any]): Values
}
sealed trait AttrOf[V,D <: AttrDiff] extends Attr {
  type Value = V
  type Diff = D
}
class OneAttr[V](val id: String) extends AttrOf[V,OneAttrDiff[V]] {
  type Values = Option[V]
  def castDiff(ad: Option[AttrDiff]) = ad.map(_.asInstanceOf[OneAttrDiff[V]])
  def diff(diff: Option[Option[V]], value: V, added: Boolean) =
    if(!added) OneAttrDiff[V](None) else OneAttrDiff(Some(value))

  def castValues(v: Option[Any]) = v.map(_.asInstanceOf[V])
}
class ManyAttr[V](val id: String) extends AttrOf[V,ManyAttrDiff[V]] {
  type Values = Set[V]
  def castDiff(ad: Option[AttrDiff]) = ad.map(_.asInstanceOf[ManyAttrDiff[V]])
  def diff(diff: Option[Map[V,Boolean]], value: V, added: Boolean) =
    ManyAttrDiff(diff.getOrElse(Map[V,Boolean]()) + (value -> added))

  def castValues(v: Option[Any]) = v.map(_.asInstanceOf[Set[V]]).getOrElse(Set[V]())
}