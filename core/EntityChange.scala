package shard

sealed trait EntityChange {
  val id: Long
  def toJSON: String
}
case class Upserted(id: Long, changes: Map[String,Any] = Map[String,Any]()) extends EntityChange {
  def get[A <: Attr](attr: A) = attr.castDiff(changes.get(attr.id.toString))

  def add[V,R,D](attr: AttrOf[V,R,D], value: V, added: Boolean)(implicit s: State) =
    attr.diff(s.lookup(id, attr), get(attr), value, added).map { diff =>
      new Upserted(id, changes + (attr.id -> diff))
    }.getOrElse(this)

  def toJSON = changes.map { av =>
    val (attrId,value) = av
    val valueString = value match {
      case value: Map[Any,Boolean] => value.map { (va) =>
        val (value,added) = va
        s"[$value,$added]"
      }.mkString("[",",","]")
      case value: Option[Any] => value.map(_.toString).getOrElse("null")
    }
    s""""$attrId":$valueString"""
  }.mkString("{",",","}")
}
case class Removed(id: Long) extends EntityChange {
  def toJSON = id.toString
}