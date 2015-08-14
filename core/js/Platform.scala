package shard.js

import scala.reflect.ClassTag

class Platform extends shard.PlatformOf[Platform] {
  trait Col {
    type Value
  }
  case class Column[V]() extends Col {
    type Value = V
  }

  type UnderlyingRow = scalajs.js.Array[Any]
  type UnderlyingDiff = scalajs.js.Array[Any]

  def newArray[A](len: Int)(implicit ct: ClassTag[A]) = new scalajs.js.Array[A](len)

  type Rows[A] = scalajs.js.Array[A]
}