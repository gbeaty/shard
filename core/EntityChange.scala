package shard

sealed trait EntityChange {
  val id: Long
}
class Upserted(val id: Long, val changes: Map[String,Any] = Map[String,Any]()) extends EntityChange {
  def get[A <: Attr](attr: A) = attr.castDiff(changes.get(attr.id.toString))

  def add[V,R,D](attr: AttrOf[V,R,D], value: V, added: Boolean)(implicit s: State) =
    attr.diff(s.lookup(id, attr), get(attr), value, added).map { diff =>
      new Upserted(id, changes + (attr.id -> diff))
      }.getOrElse(this)
}
class Removed(val id: Long) extends EntityChange