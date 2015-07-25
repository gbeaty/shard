package shard

import boopickle._

trait AsCol[A] {
  type Value
  // val pickler: Pickler[Value]
}

trait AsRow[A] {
  def apply(index: Int): Any
}

trait Platform {
  type RowData
  type DiffData

  type Col <: {
    type Value
    val pickler: Pickler[Value]
  }

  trait CList {
    type All <: Col

    val toSeq: Seq[Col]
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
    lazy val toSeq = head +: tail.toSeq
  }
  object CNil extends CList {
    type All = Col

    def ::[C <: Col](col: C) = new ColsOf(col, this)
    lazy val toSeq = Seq[Col]()
  }

  case class Row[C <: CList](data: RowData) {
    def diff(next: Row[C]) = Diff[C](diffData(data, next.data))
  }
  case class Diff[C <: CList](data: DiffData) {
    def apply(row: Row[C]) = Row[C](updateData(row.data, data))
  }

  def getField(data: RowData, index: Int): Any
  def diffData(prev: RowData, next: RowData): DiffData
  def updateData(row: RowData, diff: DiffData): RowData  

  type Rows[_]
  // type Changes[_]
}