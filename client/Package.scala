package shard

import scalajs._

package object client {
  type Version = Long

  def lookup[A <: Attr](id: Long, attr: A)(implicit v: Version): A#Returned = attr.castReturn(None)
}