package shard

import datomisca._
import boopickle._
import java.util.Date
import java.net.URI
import java.util.UUID

package object server extends shard.Platform {
  val platform = this

  trait Col {
    type Value
    val pickler: Pickler[Value]
    def apply(entity: Entity): Option[Value]
  }

  type RowData = Array[Any]
  type DiffData = Array[Option[Any]]

  def getField(data: RowData, index: Int) = data(index)

  def diffData(prev: RowData, next: RowData) = {    
    val size = prev.size
    val res = new Array[Option[Any]](size)

    var i = 0
    while(i < size) {
      val nextVal = next(i)
      if(prev(i) != nextVal) {
        res(i) = Some(nextVal)
      } else {
        res(i) = None
      }

      i += 1
    }

    res
  }
  // next.filter(kv => prev(kv._1) != kv._2)
  def updateData(row: RowData, diff: DiffData) = {
    val size = row.size
    val res = new Array[Any](size)

    var i = 0
    while(i < size) {
      res(i) = diff(i).getOrElse(row(i))
    }
    res
  }

  type Rows[A] = Seq[A]

  case class AttributeCol[V,DD <: AnyRef]
  (attribute: Attribute[DD,Cardinality.one.type])
  (implicit attrC: Attribute2EntityReaderInj[DD,Cardinality.one.type,V], val pickler: Pickler[V]) extends Col {
    type Value = V
    def apply(entity: Entity) = entity.get(attribute)
  }

  /*implicit def toAttr[DD,C <: Cardinality,V,D[_] <: AttrDiff]
    (attr: Attribute[DD,C])(implicit at: AttributeType[DD,V], ag: AttrGen[C,D]) = ag(attr)

  def toStringPickler[A] = new Pickler[A] {
    def pickle(obj: A)(implicit state: PickleState) = BasicPicklers.StringPickler.pickle(obj.toString) 
  }
  implicit val bigDecPickler = toStringPickler[BigDecimal]
  implicit val bigIntPickler = toStringPickler[BigInt]
  implicit val keywordPickler = toStringPickler[Keyword]
  implicit val uriPickler = toStringPickler[URI]
  implicit val datePickler = new Pickler[Date] {
    def pickle(obj: Date)(implicit state: PickleState) = BasicPicklers.LongPickler.pickle(obj.getTime)
  }*/
}