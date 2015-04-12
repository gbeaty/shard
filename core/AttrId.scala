package sync

sealed trait AttrId extends Any {
  val id: Long
  type Value
  type Got
}
sealed trait AttrIdOf[A,C[_]] extends Any with AttrId {
  type Value = A
  type Got = C[A]
}
case class OneAttrId[A](id: Long) extends AnyVal with AttrIdOf[A,Option]
case class ManyAttrId[A](id: Long) extends AnyVal with AttrIdOf[A,Set]

case class FactChange[A](v: Map[A,Boolean]) {
  def merge(next: FactChange[A]) = {
    val map = next.v.foldLeft(v) { (res,kv) =>
      val (value, added) = kv
      v.get(value).map(prevAdd =>
        if(prevAdd == added)
          res
        else
          res - value
      ).getOrElse(res + (value -> added))
    }
    if(map.size == 0)
      None
    else
      Some(FactChange(map))
  }
}