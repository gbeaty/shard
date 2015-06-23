package shard

import boopickle._

trait Table[C <: Columns] {
  val id: Int
  val cols: C
  implicit val rowPickler: Pickler[Row[C]]
  implicit val rowUnpickler: Unpickler[Row[C]]
  implicit val diffPickler: Pickler[Diff[C]]
  implicit val diffUnpickler: Unpickler[Diff[C]]

  sealed trait Change {
    val id: Long
  }
  case class Insert(id: Long, row: Row[C]) extends Change
  case class Update(id: Long, diff: Diff[C]) extends Change
  case class Remove(id: Long) extends Change
}