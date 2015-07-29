package shard

import scalajs._
import boopickle._

import scala.reflect.ClassTag

package object js extends shard.Platform {
  val platform = this

  trait Col {
    type Value
    val pickler: Pickler[Value]
  }

  type UnderlyingRow = scalajs.js.Array[Any]
  type UnderlyingDiff = scalajs.js.Array[Any]

  def newArray[A](len: Int)(implicit ct: ClassTag[A]) = new scalajs.js.Array[A](len)

  type Rows[A] = scalajs.js.Array[A]
}