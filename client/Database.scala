package sync.client

case class Version(v: Long) extends AnyVal

trait Database {
  def version: Version
  def applyChanges(cs: Changeset)
}