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
  type RowOf <: Row
  type DiffOf <: Diff

  def ::[V](col: ColOf[V]) = new CNel(col, this)
}
class CNel[V,T <: Columns](col: ColOf[V], tail: T) extends Columns {
  type Head = V
  type Tail = T

  type RowOf = RNel[V,T#RowOf]
  type DiffOf = RowOf#DiffOf
}
object CNil extends Columns {
  type RowOf = RNil.type
  type DiffOf = DNil.type
}

sealed trait Row {
  type DiffOf <: Diff
}
case class RNel[V,T <: Row](value: V, tail: T) extends Row {
  type DiffOf = DNel[V,T#DiffOf]
}
object RNil extends Row {
  type DiffOf = DNil.type
}

sealed trait Diff {
  type RowOf <: Row

  def apply(r: RowOf): RowOf
}
case class DNel[V,T <: Diff](value: Option[V], tail: T) extends Diff {
  type Base = V
  type Value = Option[V]
  type Tail = T

  type RowOf = RNel[Base,T#RowOf]

  def apply(r: RowOf) = RNel(value.getOrElse(r.value), tail.apply(r.tail.asInstanceOf[tail.RowOf]))
}
object DNil extends Diff {
  type RowOf = RNil.type
  def apply(r: RowOf) = RNil
}