package sync.client

case class Changeset(beforeDb: Long, afterDb: Long, changes: Map[EntityId,EntityChange])