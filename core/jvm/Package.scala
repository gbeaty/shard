package shard

import datomisca._
import boopickle._
import java.util.Date
import java.net.URI
import java.util.UUID

package object server {
  type One = Cardinality.one.type
  type Many = Cardinality.many.type
  type Version = datomisca.Database

  implicit class DatomicState(val db: datomisca.Database) extends State {
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
  }

  implicit def toOneAttr[DD,V](attr: Attribute[DD,Cardinality.one.type])(implicit at: AttrType[DD,V]) =
    new OneAttr[V](attr.ident.toString)(at.pickler)

  implicit def toManyAttr[DD,V](attr: Attribute[DD,Cardinality.many.type])(implicit at: AttrType[DD,V]) =
    new ManyAttr[V](attr.ident.toString)(at.pickler)

  /*implicit def toAttr[DD,C <: Cardinality,V]
    (attr: Attribute[DD,C])(implicit at: AttrType[DD,V]) =
      if(attr.cardinality == Cardinality.one)
        new OneAttr[V](attr.ident.toString)(at.pickler)
      else
        new ManyAttr[V](attr.ident.toString)(at.pickler)*/

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
  
  implicit val bigDecAttrType = new AttrType[SchemaType.bigdec.type,BigDecimal]
  implicit val bigIntAttrType = new AttrType[SchemaType.bigint.type,BigInt]
  implicit val booleanAttrType = new AttrType[SchemaType.boolean.type,Boolean]
  implicit val bytesAttrType = new AttrType[SchemaType.bytes.type,Array[Byte]]
  implicit val doubleAttrType = new AttrType[SchemaType.double.type,Double]
  implicit val floatAttrType = new AttrType[SchemaType.float.type,Float]
  implicit val instantAttrType = new AttrType[SchemaType.instant.type,Date]
  implicit val keywordAttrType = new AttrType[SchemaType.keyword.type,Keyword]
  implicit val longAttrType = new AttrType[java.lang.Long,Long]
  implicit val refAttrType = new AttrType[SchemaType.ref.type,Long]
  implicit val stringAttrType = new AttrType[SchemaType.string.type,String]
  implicit val uriAttrType = new AttrType[SchemaType.uri.type,URI]
  implicit val uuidAttrType = new AttrType[SchemaType.uuid.type,UUID]

  sealed class AttrCard[C <: Cardinality,A <: Attr]
  // implicit val oneCard = new AttrCard[Cardinality.one.type,OneAttr]
}