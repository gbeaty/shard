package shard

trait Col {
  type Value
}
trait ColOf[V] extends Col {
  type Value = V
}

trait CList {
  type All <: Col
}
trait Cols extends CList {
  type Head <: Col
  type Tail <: CList
  type All = Head with Tail#All
  type Type = Head#Value

  val head: Head
  val tail: Tail
}
case class ColsOf[H <: Col, T <: CList](head: H, tail: T) extends Cols {
  type Head = H    
  type Tail = T

  def ::[C <: Col](col: C) = new ColsOf(col, this)
}
object CNil extends CList {
  type All = Col

  def ::[C <: Col](col: C) = new ColsOf(col, this)
}

trait Platform {
  type RowData
  type DiffData

  case class Row[C <: CList](data: RowData) {
    def diff(next: Row[C]) = Diff[C](diffData(data, next.data))
  }
  case class Diff[C <: CList](data: DiffData) {
    def apply(row: Row[C]) = Row[C](updateData(row.data, data))
  }

  def diffData(prev: RowData, next: RowData): DiffData
  def updateData(row: RowData, diff: DiffData): RowData
}