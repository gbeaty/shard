package shard

sealed trait Col {
  val name: String
  type Value
}
case class ColOf[V](name: String) extends Col {
  type Value = V
}
object Col {
  def apply[V](n: String) = new ColOf[V](n)
}

trait Columns {
  type Self <: Columns
  type RowOf <: Row
  type DiffOf <: Diff

  def ::[V](col: ColOf[V]): CNel[V,Self]
}
class CNel[V,T <: Columns](col: ColOf[V], tail: T) extends Columns {
  type Self = CNel[V,T]
  type Head = V
  type Tail = T

  type RowOf = RNel[V,T#RowOf]
  type DiffOf = RowOf#DiffOf

  def ::[V](col: ColOf[V]) = new CNel(col, this)
}
object CNil extends Columns {
  type Self = CNil.type
  type RowOf = RNil.type
  type DiffOf = DNil.type

  def ::[V](col: ColOf[V]) = new CNel(col, this)
}

sealed trait Row {
  type Self <: Row
  type DiffOf <: Diff

  def diff(next: Self): DiffOf
}
case class RNel[V,T <: Row](head: V, tail: T) extends Row {
  type Self = RNel[V,T]
  type DiffOf = DNel[V,T#DiffOf]

  def diff(next: Self) = DNel(if(head == next.head) None else Some(next.head), tail.diff(next.tail.asInstanceOf[tail.Self]))
}
object RNil extends Row {
  type Self = RNil.type
  type DiffOf = DNil.type

  def diff(next: Self) = DNil
}

sealed trait Diff {
  type RowOf <: Row

  def apply(r: RowOf): RowOf
}
case class DNel[V,T <: Diff](head: Option[V], tail: T) extends Diff {
  type Base = V
  type Value = Option[V]
  type Tail = T

  type RowOf = RNel[Base,T#RowOf]

  def apply(r: RowOf) = RNel(head.getOrElse(r.head), tail.apply(r.tail.asInstanceOf[tail.RowOf]))
}
object DNil extends Diff {
  type RowOf = RNil.type
  def apply(r: RowOf) = RNil
}