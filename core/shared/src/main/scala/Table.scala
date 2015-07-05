package shard

import boopickle._

trait Table[P <: Platform, C <: P#Cols] {
  val id: Int
  val cols: C
  implicit val rowPickler: Pickler[P#Row[C]]
  implicit val rowUnpickler: Unpickler[P#Row[C]]
  implicit val diffPickler: Pickler[P#Diff[C]]
  implicit val diffUnpickler: Unpickler[P#Diff[C]]

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

trait WriteTable[P <: Platform, C <: P#Cols] extends Table[P,C] {
  def transact(cs: Seq[Change])
}