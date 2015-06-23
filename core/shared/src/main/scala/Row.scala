package shard

sealed trait Col {
  type Value
}
case class ColOf[V]() extends Col {
  type Value = V
}

sealed trait Columns {
  type All <: Col

  // def ::[C <: Col](col: C): CNelOf[C,Self]
  val size: Int

  def index = size - 1
}
trait CNel extends Columns {
  type Head <: Col
  type Tail <: Columns

  type All = Head with Tail#All
}
class CNelOf[H <: Col,T <: Columns](col: Col, tail: T) extends CNel {  
  type Head = H
  type Tail = T

  def ::[C <: Col](col: C) = new CNelOf[C,this.type](col, this)
  val size = tail.size + 1
}
case object CNil extends Columns {
  type All = Col

  def ::[C <: Col](col: C) = new CNelOf[C,this.type](col, this)
  val size = 0
}

case class Row[C <: Columns](fields: Map[Col,Any]) {
  def diff(next: Row[C]) = Diff[C](next.fields.filter(kv => fields(kv._1) != kv._2))

  def get[A <: C#All](col: A) = fields(col).asInstanceOf[A#Value]
}
case class Diff[C <: Columns](diffs: Map[Col,Any]) {  
  def apply(row: Row[C]) = Row[C](row.fields ++ diffs)

  def get[A <: C#All](col: A) = diffs.get(col).map(_.asInstanceOf[A#Value])
}