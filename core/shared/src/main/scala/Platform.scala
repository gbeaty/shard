package shard

import boopickle._
import scala.collection.mutable.ArrayLike

import scala.reflect._

sealed trait RList {
  type This <: RList
  type Diff <: RList

  def diff(next: This): Diff
  def update(diff: Diff): This
  def apply(i: Int): Any
}
sealed trait Row extends RList {
  type Head
  type Tail <: RList  
}
case class RowOf[H,T <: RList](value: H, tail: T) extends Row {
  type This = RowOf[H,T]
  type Head = H
  type Tail = T
  type Diff = RowOf[Option[Head],Tail#Diff]

  def ::[V](value: V) = RowOf(value, this)
  // def toRow[P <: Platform, C <: Cols[P]]

  override def equals(that: Any): Boolean = that match {
    case RowOf(v,t) => if(v == t) tail.equals(t) else false
    case _ => false
  }

  def diff(next: This) =
    RowOf[Option[H],T#Diff](
      if(value == next.value) None else Some(next.value),
      tail.diff(next.tail.asInstanceOf[tail.This])
    )

  def update(diff: Diff) =
    RowOf(diff.value.getOrElse(value), tail.update(diff.tail.asInstanceOf[tail.Diff]).asInstanceOf[T])

  def apply(i: Int) = if(i == 0) value else tail.apply(i - 1)
}
object RNil extends RList {
  type This = RNil.type
  type Diff = RNil.type

  def ::[V](value: V) = RowOf(value, RNil)

  override def equals(that: Any): Boolean = that match {
    case RNil => true
    case _ => false
  }

  def diff(next: This) = RNil
  def update(diff: Diff) = RNil
  def apply(i: Int) = RNil
}

trait CList {
  type All <: Col
  type Row <: shard.RList
  type Diff = Row#Diff

  val toSeq: Seq[Col]
  val length: Int
}
trait Cols extends CList {
  type Head <: Col
  type Tail <: CList
  type All = Head with Tail#All
  type Row = RowOf[Head#Value,Tail#Row]
  type Type = Head#Value

  val head: Head
  val tail: Tail

  val length: Int
}
case class ColsOf[H <: Col, T <: CList](head: H, tail: T) extends Cols {
  type Head = H
  type Tail = T  

  def ::[C <: Col](col: C) = new ColsOf[C,ColsOf[H,T]](col, this)
  lazy val toSeq = head +: tail.toSeq
  val length = 1 + tail.length
}
object CNil extends CList {
  type All = Col
  type Row = RNil.type

  def ::[C <: Col](col: C) = new ColsOf[C,CNil.type](col, this)
  lazy val toSeq = Seq[Col]()
  final val length = 0
}

trait Col {
  type Value
}
case class ColOf[V](id: String) extends Col {
  type Value = V
}

trait Platform {
  type This <: Platform
  // type RowData = ArrayLike[Any,_]
  // type DiffData = ArrayLike[Option[Any],_]

  // type Row[C <: Cols] = shard.Row[This,C]
  // type Diff[C <: Cols] = shard.Diff[This,C]

  def newArray[A](len: Int)(implicit ct: ClassTag[A]): ArrayLike[A,_]

  type Rows[_]
  // type Changes[_]
}

trait PlatformOf[P <: PlatformOf[P]] extends Platform {
  type This = P
}