package shard.client

import shard._

case class Entity(id: Long, data: Map[String,Any]) {
  def get(attr: Attr) = attr.castValues(data.get(attr.id))
}