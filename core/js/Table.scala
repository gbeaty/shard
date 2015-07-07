package shard.js

import shard._
import boopickle._
import scalajs.js._

class Table[C <: Cols]
  (val id: Int, val cols: C)
  (implicit val rowPickler: Pickler[Row[C]], val rowUnpickler: Unpickler[Row[C]],
            val diffPickler: Pickler[Diff[C]], val diffUnpickler: Unpickler[Diff[C]]) extends shard.WriteTable[C,Platform.type] {

    var _rows = Dictionary[Row[C]]()
    val rows = _rows
    private var _version: Option[Long] = None
    def version = _version

    def refresh(newVersion: Long, newRows: Dictionary[Row[C]]) {
      _rows = newRows
      _version = Some(newVersion)
    }
    
    def transact(cs: Seq[Change]) = {
      val result = cs.flatMap(change(_))
    }

    def change(c: Change): Option[(Change, ChangeError)] = {
      val id = c.id.toString
      c match {
        case c: Insert => if(_rows.contains(id)) {
          Some(c -> KeyAlreadyExists)
        } else {
          _rows += (id -> c.row)
          None
        }
        case c: Update => if(_rows.contains(id)) {
          _rows += (id -> c.diff(_rows(id)))
          None
        } else {
          Some(c -> KeyNotFound)
        }
        case c: Remove => if(_rows.contains(id)) {
          _rows - id
          None
        } else {
          Some(c -> KeyNotFound)          
        }
      }
    }
  }