package sync

trait Attr {
  type Value
  type Returns
  type Change
  val ident: String
}
sealed trait AttrOf[V,R,C] extends Attr {
  type Value = V
  type Returns = R
  type Change = C
}
class OneAttr[V](val ident: String) extends AttrOf[V,Option[V],Option[V]]
class ManyAttr[V](val ident: String) extends AttrOf[V,Set[V],Map[V,Boolean]]

trait AttrChanges {
  type Value
  type Returns
  type Change
  val attr: AttrOf[Value,Returns,Change]
  val change: Change
}
class AttrChangesOf[V,R,C](val attr: AttrOf[V,R,C], val change: C) extends AttrChanges {
  type Value = V
  type Returns = R
  type Change = C
}

trait Entity[Self <: Entity[Self]] {
  val id: Long
  def getAny(attrIdent: String): Option[Any]
  def setData(attrIdent: String, data: Any): Self
  
  def get[V,R,C](attr: AttrOf[V,R,C]): R = {
    val value = getAny(attr.ident)
    attr match {
      case attr: OneAttr[V] => value.map(_.asInstanceOf[V])
      case attr: ManyAttr[V] => value.map(_.asInstanceOf[Set[V]]).getOrElse(Set[V]())
    }
  }

  def apply(changes: AttrChanges*): Self
}

trait State {
}