package shard

class ServerChangeset(val versionBefore: Long, val versionAfter: Long, val changes: Map[Long,EntityChange])