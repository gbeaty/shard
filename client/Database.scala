package sync.client

trait Entities {
  def version: Long
  def applyChanges(cs: Changeset)
}