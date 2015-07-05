package shard

trait Platform {
  type Col <: {
    type Value
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

  type Row[C <: CList]
  type Diff[C <: CList]

  def diff[C <: Cols](a: Row[C], b: Row[C]): Diff[C]
  def update[C <: Cols](row: Row[C], diff: Diff[C]): Row[C]
}