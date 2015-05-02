package shard

trait State {
  def lookup[A <: Attr](id: Long, attr: A): A#Returned
}