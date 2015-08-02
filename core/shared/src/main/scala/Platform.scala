package shard

import boopickle._
import scala.collection.mutable.ArrayLike

import scala.reflect._

trait Platform {
  type RowData = ArrayLike[Any,_]
  type DiffData = ArrayLike[Option[Any],_]

  type Col <: {
    type Value
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
  trait CNil extends CList {
    type All = Col

    def ::[C <: Col](col: C) = new ColsOf(col, this)
    lazy val toSeq = Seq[Col]()
  }
  object CNil extends CNil

  case class Row[C <: CList](data: RowData) {
    def apply(i: Int) = data(i)

    override def equals(that: Any): Boolean = that match {
      case diff: Row[C] => { // data.equals(diff.data)
        var i = 0
        while(i < data.length) {
          if(data(i) != diff.data(i)) {
            return false
          }
          i += 1
        }
        true
      }
      case _ => false
    }

    def diff(next: Row[C]) = {
      var i = 0
      val res = newArray[Option[Any]](data.length)
      while(i < data.length) {
        val nf = next(i)        
        res(i) = if(data(i) == nf) None else Some(nf)
        i += 1
      }
      Diff[C](res)
    }
  }
  case class Diff[C <: CList](data: DiffData) {
    def apply(i: Int) = data(i)

    override def equals(that: Any): Boolean = that match {
      case diff: Diff[C] => {
        var i = 0
        while(i < data.length) {
          if(data(i) != diff.data(i)) {
            return false
          }
          i += 1
        }
        true
      }
      case _ => false
    }

    def apply(row: Row[C]) = {
      var i = 0
      val res = newArray[Any](data.length)
      while(i < data.length) {
        val d = data(i)
        res(i) = d.getOrElse(row(i))
        i += 1
      }
      Row[C](res)
    }
  }

  def newArray[A](len: Int)(implicit ct: ClassTag[A]): ArrayLike[A,_]

  type Rows[_]
  // type Changes[_]
}