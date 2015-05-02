package shard

sealed trait Attr {
  type Value
  type Returned
  type Diff <: AttrDiff
  val id: String
  def castReturn(a: Any): Returned
  def castDiff(ad: Option[AttrDiff]): Option[Diff]
  def diff(orig: Returned, diff: Option[Diff#Value], value: Value, added: Boolean): Option[Diff]
}
class OneAttr[V](val id: String) extends Attr {
  type Value = V
  type Returned = Option[V]
  type Diff = OneAttrDiff[V]
  def castReturn(a: Any) = a.asInstanceOf[Option[V]]
  def castDiff(ad: Option[AttrDiff]) = ad.map(_.asInstanceOf[OneAttrDiff[V]])
  def diff(orig: Option[V], diff: Option[Option[V]], value: V, added: Boolean) =
    if(!added)
      None
    else
      if(orig.exists(_ == value)) None else Some(OneAttrDiff(Some(value)))
}
class ManyAttr[V](val id: String) extends Attr {
  type Value = V
  type Returned = Set[V]
  type Diff = ManyAttrDiff[V]
  def castReturn(a: Any) = a.asInstanceOf[Set[V]]
  def castDiff(ad: Option[AttrDiff]) = ad.map(_.asInstanceOf[ManyAttrDiff[V]])
  def diff(orig: Set[V], diff: Option[Map[V,Boolean]], value: V, added: Boolean) = {
    val oldDiffMap = diff.getOrElse(Map[V,Boolean]())
    val newDiffMap = if(added)
      if(orig.contains(value))
        oldDiffMap - value
      else
        oldDiffMap + (value -> true)
    else
      if(orig.contains(value))
        oldDiffMap + (value -> false)
      else
        oldDiffMap - value

    if(newDiffMap.size == 0)
      Some(ManyAttrDiff(newDiffMap))
    else
      None
  }
}