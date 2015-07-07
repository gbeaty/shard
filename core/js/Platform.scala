package shard.js

import scala.scalajs._

import shard.CList

object Platform extends shard.Platform {
  type RowData = js.Array[Any]
  type DiffData = js.Array[Any]

  def diffData(prev: RowData, next: RowData) = {
    var i = 0
    val res = js.Array[Any]()
    while(i < prev.length) {
      val nf = next(i)
      if(prev(i) != nf) {
        res(i) = nf
      }
      i += 1
    }
    res
  }

  def updateData(row: RowData, diff: DiffData) = {
    var i = 0
    val res = js.Array[Any]()
    while(i < row.length) {
      val d = diff(i)
      res(i) = if(d == null) row(i) else d
      i += 1
    }
    res
  }
}