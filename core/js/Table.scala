package shard.js

import shard._
import boopickle._
import scalajs.js._

class Table[C <: Columns]
  (val id: Int, val cols: C)
  (implicit val rowPickler: Pickler[Row[C]], val rowUnpickler: Unpickler[Row[C]],
            val diffPickler: Pickler[Diff[C]], val diffUnpickler: Unpickler[Diff[C]]) extends shard.Table[C] {

    val rows = Dictionary[Row[C]]()
    
    def update(c: Change) = {
      val id = c.id.toString
      c match {
        case c: Insert => rows += (id -> c.row)
        case c: Update => rows += (id -> c.diff(rows(id)))
        case c: Remove => rows - id
      }
    }
  }