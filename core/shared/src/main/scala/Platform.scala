package shard

import boopickle._
import scala.collection.mutable.ArrayLike

import scala.reflect._

trait CList[P <: Platform] {
  type All <: P#Col

  val toSeq: Seq[P#Col]
}
trait Cols[P <: Platform] extends CList[P] {
  type Head <: P#Col
  type Tail <: CList[P]
  type All = Head with Tail#All
  type Type = Head#Value

  val head: Head
  val tail: Tail    
}
case class ColsOf[P <: Platform, H <: P#Col, T <: CList[P]](head: H, tail: T) extends Cols[P] {
  type Head = H    
  type Tail = T

  def ::[C <: P#Col](col: C) = new ColsOf[P,C,ColsOf[P,H,T]](col, this)
  lazy val toSeq = head +: tail.toSeq
}
class CNil[P <: Platform] extends CList[P] {
  type All = P#Col

  def ::[C <: P#Col](col: C) = new ColsOf[P,C,CNil[P]](col, this)
  lazy val toSeq = Seq[P#Col]()
}

case class Row[P <: Platform, C <: Cols[P]](data: P#RowData) {
  def apply(i: Int) = data(i)

  override def equals(that: Any): Boolean = that match {
    case diff: Row[P,C] => { // data.equals(diff.data)
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

  def diff(next: Row[P,C])(implicit platform: P) = {
    var i = 0
    val res = platform.newArray[Option[Any]](data.length)
    while(i < data.length) {
      val nf = next(i)        
      res(i) = if(data(i) == nf) None else Some(nf)
      i += 1
    }
    Diff[P,C](res)
  }
}
case class Diff[P <: Platform, C <: Cols[P]](data: P#DiffData) {
  def apply(i: Int) = data(i)

  override def equals(that: Any): Boolean = that match {
    case diff: Diff[P,C] => {
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

  def apply(row: Row[P,C])(implicit platform: P) = {
    var i = 0
    val res = platform.newArray[Any](data.length)
    while(i < data.length) {
      val d = data(i)
      res(i) = d.getOrElse(row(i))
      i += 1
    }
    Row[P,C](res)
  }
}

trait Platform {
  type This <: Platform
  type RowData = ArrayLike[Any,_]
  type DiffData = ArrayLike[Option[Any],_]

  type CList = shard.CList[This]
  type Cols = shard.Cols[This]
  type ColsOf[H <: This#Col, T <: CList] = shard.ColsOf[This, H, T]
  type CNil = shard.CNil[This]
  object CNil extends shard.CNil[This]

  type Row[C <: Cols] = shard.Row[This,C]
  type Diff[C <: Cols] = shard.Diff[This,C]

  type Col <: {
    type Value
  }

  def newArray[A](len: Int)(implicit ct: ClassTag[A]): ArrayLike[A,_]

  type Rows[_]
  // type Changes[_]
}

trait PlatformOf[P <: PlatformOf[P]] extends Platform {
  type This = P
}