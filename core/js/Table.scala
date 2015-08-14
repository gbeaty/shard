package shard.js

import shard._
import boopickle._
import scalajs.js._

class Table[C <: Cols[Platform]]
  (val id: Int, val cols: C)
  (implicit val rowPickler: Pickler[shard.Row[Platform,C]],
            val diffPickler: Pickler[shard.Diff[Platform,C]]) extends shard.WriteTable[C,Platform] {

    var _rows = Dictionary[Row]()
    val rows = _rows
    private var _version: Option[Long] = None
    def version = _version

    def refresh(newVersion: Long, newRows: Dictionary[Row]) {
      _rows = newRows
      _version = Some(newVersion)
    }

    def transact(cs: Changeset): Boolean =
      if(_version.forall(_ == cs.beforeVersion)) {
        val test: scalajs.js.Array[Change] = cs.changes
        if(cs.changes.forall(checkChange(_))) {          
          cs.changes.foreach(change(_))
          _version = Some(cs.afterVersion)
          true
        } else {
          false
        }
      } else {
        false
      }

    // Since each (potential) entity should have only 1 change, we can check their validity ahead of time.
    def checkChange(c: Change) = c match {
      case c: Insert => !_rows.contains(id.toString)
      case c: Update => _rows.contains(id.toString)
      case c: Remove => !_rows.contains(id.toString)
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