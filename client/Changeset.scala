package sync.client

case class Changeset(beforeDb: Version, afterDb: Version, changes: Map[EntityId,EntityChange])