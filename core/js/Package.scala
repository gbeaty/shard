package shard

import scalajs._

package object js extends shard.js.Platform {
  val platform = this

  trait Col {
    type Value
    val pickler: Pickler[Value]
  }

  type UnderlyingRow = scalajs.js.Array[Any]
  type UnderlyingDiff = scalajs.js.Array[Any]

  def newArray[A](len: Int)(implicit ct: ClassTag[A]) = new scalajs.js.Array[A](len)
}