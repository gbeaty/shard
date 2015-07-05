package shard.js

import scala.scalajs._

object Platform extends shard.Platform {
  trait Col {
    type Value  
  }
  case class ColOf[V]() extends Col {
    type Value = V
  }

  case class Row[C <: CList](array: js.Array[Any])
  case class Diff[C <: CList](array: js.Array[Any])

  def diff[C <: CList](last: Row[C], next: Row[C]) = {
    var i = 0
    val res = js.Array[Any]()
    while(i < last.array.length) {
      val nf = next.array(i)
      if(last.array(i) != nf) {
        res(i) = nf
      }
      i += 1
    }
    Diff[C](res)
  }
  def update[C <: CList](row: Row[C], diff: Diff[C]) = {
    var i = 0
    val res = js.Array[Any]()
    while(i < row.array.length) {
      val d = diff.array(i)
      res(i) = if(d == null) row.array(i) else d
      i += 1
    }
    Row[C](res)
  }
}