package sync.client

case class Version(v: Long) extends AnyVal

trait Entities {
  def version: Version
  def applyChanges(cs: Changeset)
}