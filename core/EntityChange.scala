package sync

trait EntityChange[I] {
  val entity: I
}
case class Inserted[I](entity: I) extends EntityChange[I]
case class Updated[I](before: I, after: I, changes: Map[Int,FactChange[_]]) extends EntityChange[I] {
  val entity = after
  def merge(next: Updated[I]) = {
    val prevChanges = changes.asInstanceOf[Map[Int,FactChange[Any]]]
    val nextChanges = next.changes.asInstanceOf[Map[Int,FactChange[Any]]]
    val res = nextChanges.foldLeft(prevChanges) { (res,kv) =>
      val attrId = kv._1
      val nextChange = kv._2
      prevChanges.get(attrId).map( oc =>
        oc.merge(nextChange).map(m => res + (attrId -> m)).getOrElse(res - attrId)
      ).getOrElse(res + kv)
    }
    if(res.size == 0)
      None
    else    
      Some(Updated(
        before,
        next.after,
        res
      ))
  }
}
case class Removed[I](entity: I) extends EntityChange[I]