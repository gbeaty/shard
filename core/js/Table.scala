package shard.js

import shard._
import boopickle._
import scalajs.js._

class Table[C <: Platform.Cols]
  (val id: Int, val cols: C)
  (implicit val rowPickler: Pickler[Platform.Row[C]], val rowUnpickler: Unpickler[Platform.Row[C]],
            val diffPickler: Pickler[Platform.Diff[C]], val diffUnpickler: Unpickler[Platform.Diff[C]]) extends shard.WriteTable[Platform.type,C] {

    var _rows = Dictionary[Platform.Row[C]]()
    val rows = _rows
    private var _version: Option[Long] = None
    def version = _version

    def refresh(newVersion: Long, newRows: Dictionary[Platform.Row[C]]) {
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
          _rows += (id -> Platform.update(_rows(id), c.diff))
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