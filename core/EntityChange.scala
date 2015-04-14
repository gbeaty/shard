package sync

trait EntityChange[D]
case class Inserted[D](data: D) extends EntityChange[D]
case class Updated[D](changes: Map[Int,FactChange[_]]) extends EntityChange[D] {
  def merge(next: Updated[D]) = {
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
      Some(Updated[D](res))
  }
}
case class Removed[D]() extends EntityChange[D]