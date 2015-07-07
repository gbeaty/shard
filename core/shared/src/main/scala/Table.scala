package shard

import boopickle._

trait Table[C <: Cols, P <: Platform] {
  val id: Int
  val cols: C
  /*implicit val rowPickler: Pickler[Row[C]]
  implicit val rowUnpickler: Unpickler[Row[C]]
  implicit val diffPickler: Pickler[Diff[C]]
  implicit val diffUnpickler: Unpickler[Diff[C]]*/

  sealed trait Change {
    val id: Long
  }
  case class Insert(id: Long, row: P#Row[C]) extends Change
  case class Update(id: Long, diff: P#Diff[C]) extends Change
  case class Remove(id: Long) extends Change

  trait ChangeError
  case object KeyAlreadyExists extends ChangeError
  case object KeyNotFound extends ChangeError
}

trait WriteTable[C <: Cols, P <: Platform] extends Table[C,P] {
  def transact(cs: Seq[Change])
}