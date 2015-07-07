package shard

import datomisca._
import boopickle._
import java.util.Date
import java.net.URI
import java.util.UUID

package object server {
  type Row[C <: CList] = Platform.Row[C]
  type Diff[C <: CList] = Platform.Diff[C]
  //type Cols = shard.Cols[shard.server.Col]

  type One = Cardinality.one.type
  type Many = Cardinality.many.type
  type Version = datomisca.Database

  /*implicit class DatomicState(val db: datomisca.Database) extends State {
    def lookup[A <: Attr](id: Long, attr: A) = attr.castReturn({
      val res = db.entity(id).entity.get(attr.id)
      attr match {
        case attr: OneAttr[A#Value] => if(res == null) None else Some(res)
        case attr: ManyAttr[A#Value] =>
          if(res == null)
            Set[Object]()
          else
            scala.collection.JavaConversions.iterableAsScalaIterable(res.asInstanceOf[java.util.Collection[Object]]).toSet
      }
    })
  }*/

  implicit def toAttr[DD,C <: Cardinality,V,D[_] <: AttrDiff]
    (attr: Attribute[DD,C])(implicit at: AttributeType[DD,V], ag: AttrGen[C,D]) = ag(attr)

  def toStringPickler[A] = new Pickler[A] {
    def pickle(obj: A)(implicit state: PickleState) = Pickler.StringPickler.pickle(obj.toString) 
  }
  implicit val bigDecPickler = toStringPickler[BigDecimal]
  implicit val bigIntPickler = toStringPickler[BigInt]
  implicit val keywordPickler = toStringPickler[Keyword]
  implicit val uriPickler = toStringPickler[URI]
  implicit val datePickler = new Pickler[Date] {
    def pickle(obj: Date)(implicit state: PickleState) = Pickler.LongPickler.pickle(obj.getTime)
  }
  
  class AttributeType[DD,V](implicit val pickler: Pickler[V])
  implicit val bigDecAttributeType = new AttributeType[java.math.BigDecimal,BigDecimal]
  implicit val bigIntAttributeType = new AttributeType[java.math.BigInteger,BigInt]
  implicit val booleanAttributeType = new AttributeType[java.lang.Boolean,Boolean]
  implicit val bytesAttributeType = new AttributeType[Array[Byte],Array[Byte]]
  implicit val doubleAttributeType = new AttributeType[java.lang.Double,Double]
  implicit val floatAttributeType = new AttributeType[java.lang.Float,Float]
  implicit val instantAttributeType = new AttributeType[Date,Date]
  implicit val keywordAttributeType = new AttributeType[Keyword,Keyword]
  implicit val longAttributeType = new AttributeType[java.lang.Long,Long]
  implicit val refAttributeType = new AttributeType[DatomicRef.type,Long]
  implicit val stringAttributeType = new AttributeType[String,String]
  implicit val uriAttributeType = new AttributeType[URI,URI]
  implicit val uuidAttributeType = new AttributeType[UUID,UUID]

  sealed abstract class AttrGen[C <: Cardinality,D[_] <: AttrDiff] {
    def apply[DD,V](attr: Attribute[DD,C])(implicit at: AttributeType[DD,V]): AttrOf[V,D[V]]
  }
  implicit val oneAttrGen = new AttrGen[Cardinality.one.type,OneAttrDiff] {
    def apply[DD,V](attr: Attribute[DD,Cardinality.one.type])(implicit at: AttributeType[DD,V]) =
      new OneAttr[V](attr.ident.toString)
  }
  implicit val manyAttrGen = new AttrGen[Cardinality.many.type,ManyAttrDiff] {
    def apply[DD,V](attr: Attribute[DD,Cardinality.many.type])(implicit at: AttributeType[DD,V]) =
      new ManyAttr[V](attr.ident.toString)
  }
}