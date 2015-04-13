package sync

trait EntityChange[I,D] {
  val id: I
}
case class Inserted[I,D](id: I, data: D) extends EntityChange[I,D]
case class Updated[I,D](id: I, changes: Map[Int,FactChange[_]]) extends EntityChange[I,D] {
  def merge(next: Updated[I,D]) = {
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
      Some(Updated[I,D](id, res))
  }
}
case class Removed[I,D](id: I) extends EntityChange[I,D]