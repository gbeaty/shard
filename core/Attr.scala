package shard

sealed trait Attr {
  type Value
  type Returned
  type Diff
  val id: String
  def castReturn(a: Any): Returned
  def castDiff(a: Any): Diff
  def diff(orig: Returned, diff: Diff, value: Value, added: Boolean): Option[Diff]
}
sealed trait AttrOf[V,R,D] extends Attr {
  type Value = V
  type Returned = R
  type Diff = D
}
class OneAttr[V](val id: String) extends AttrOf[V,Option[V],Option[V]] {
  def castReturn(a: Any) = a.asInstanceOf[Option[V]]
  def castDiff(a: Any) = a.asInstanceOf[Option[V]]
  def diff(orig: Option[V], diff: Option[V], value: V, added: Boolean) =
    if(!added)
      None
    else
      if(orig.exists(_ == value)) None else Some(Some(value))
}
class ManyAttr[V](val id: String) extends AttrOf[V,Set[V],Map[V,Boolean]] {
  def castReturn(a: Any) = a.asInstanceOf[Set[V]]
  def castDiff(a: Any) = a.asInstanceOf[Map[V,Boolean]]
  def diff(orig: Set[V], diff: Map[V,Boolean], value: V, added: Boolean) = {
    val newDiff = if(added)
      if(orig.contains(value))
        diff - value
      else
        diff + (value -> true)
    else 
      if(orig.contains(value))
        diff + (value -> false)
      else
        diff - value

    if(newDiff.size == 0)
      Some(newDiff)
    else
      None
  }
}