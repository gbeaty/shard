package shard

import boopickle._

trait Table {
  type Cols <: Platform#Cols
  type Platform <: shard.Platform

  type Row = Platform#Row[Cols]
  type Diff = Platform#Diff[Cols]
  /*type Change = shard.Change[this.type]
  type Insert = shard.Insert[this.type]
  type Update = shard.Update[this.type]
  type Remove = shard.Remove[this.type]
  type Refresh = shard.Refresh[this.type]
  type Changeset = shard.Changeset[this.type]*/
}
trait TableOf[C <: P#Cols, P <: Platform] extends Table {
  type Cols = C
  type Platform = P

  val id: Int
  val cols: C
  /*implicit val rowPickler: Pickler[Row[C]]
  implicit val rowUnpickler: Unpickler[Row[C]]
  implicit val diffPickler: Pickler[Diff[C]]
  implicit val diffUnpickler: Unpickler[Diff[C]]*/

  trait ChangeError
  case object KeyAlreadyExists extends ChangeError
  case object KeyNotFound extends ChangeError

  sealed trait Change {
    val id: Long
  }
  case class Insert(id: Long, row: Row) extends Change
  case class Update(id: Long, diff: Diff) extends Change
  case class Remove(id: Long) extends Change

  case class Refresh(beforeVersion: Long, afterVersion: Long, changes: Platform#Rows[Insert])
  case class Changeset(beforeVersion: Long, afterVersion: Long, changes: Platform#Rows[Change])
}

/*
sealed trait Change[T <: Table] {
  val id: Long
}
case class Insert[T <: Table](id: Long, row: T#Row) extends Change[T]
case class Update[T <: Table](id: Long, diff: T#Diff) extends Change[T]
case class Remove[T <: Table](id: Long) extends Change[T]

case class Refresh[T <: Table](beforeVersion: Long, afterVersion: Long, changes: T#Platform#Rows[Insert[T]])
case class Changeset[T <: Table](beforeVersion: Long, afterVersion: Long, changes: T#Platform#Rows[Change[T]])
*/

trait WriteTable[C <: P#Cols, P <: Platform] extends TableOf[C,P] {
  def transact(cs: Changeset): Boolean
}