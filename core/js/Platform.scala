package shard.js

import scala.scalajs._

import boopickle._

trait Platform extends shard.Platform {
  trait Col {
    type Value
    val pickler: Pickler[Value]
  }

  type RowData = scalajs.js.Array[Any]
  type DiffData = scalajs.js.Array[Any]

  def getField(data: RowData, index: Int) = data(index)

  def diffData(prev: RowData, next: RowData) = {
    var i = 0
    val res = scalajs.js.Array[Any]()
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
    val res = scalajs.js.Array[Any]()
    while(i < row.length) {
      val d = diff(i)
      res(i) = if(d == null) row(i) else d
      i += 1
    }
    res
  }

  type Rows[A] = scalajs.js.Array[A]//[RowData]
  // type Changes = js.Array
}