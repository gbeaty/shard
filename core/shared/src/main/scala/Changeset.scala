package shard

trait ClientUpdate {
  val versionAfter: Long
}

case class ServerChangeset(versionBefore: Long, versionAfter: Long, changes: Map[Long,EntityChange]) extends ClientUpdate

case class Refresh(versionAfter: Long, data: Map[Long,client.Entity]) extends ClientUpdate