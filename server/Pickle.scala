package shard.server

import shard._
import boopickle._
import datomisca._
import java.util.Date
import java.net.URI
import java.util.UUID

object Serialize {
  def apply(cs: ServerChangeset) = Pickle(Seq(cs.versionBefore, cs.versionAfter))//, cs.changes))

  implicit val attrPickler = CompositePickler[AttrDiff]
    .addConcreteType[OneAttrDiff[Long]]
    .addConcreteType[ManyAttrDiff[Long]]
    .addConcreteType[OneAttrDiff[String]]
    .addConcreteType[ManyAttrDiff[String]]
    .addConcreteType[OneAttrDiff[Float]]
    .addConcreteType[ManyAttrDiff[Float]]
    .addConcreteType[OneAttrDiff[Double]]
    .addConcreteType[ManyAttrDiff[Double]]

  def test(ad: AttrDiff) = Pickle.intoBytes(ad)
}

object Picklers {
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
}

trait Picklers {
  import Picklers._
  implicit val bigDecAttrType = new AttrType[SchemaType.bigdec.type,BigDecimal]
  implicit val bigIntAttrType = new AttrType[SchemaType.bigint.type,BigInt]
  implicit val booleanAttrType = new AttrType[SchemaType.boolean.type,Boolean]
  implicit val bytesAttrType = new AttrType[SchemaType.bytes.type,Array[Byte]]
  implicit val doubleAttrType = new AttrType[SchemaType.double.type,Double]
  implicit val floatAttrType = new AttrType[SchemaType.float.type,Float]
  implicit val instantAttrType = new AttrType[SchemaType.instant.type,Date]
  implicit val keywordAttrType = new AttrType[SchemaType.keyword.type,Keyword]
  implicit val longAttrType = new AttrType[SchemaType.long.type,Long]
  implicit val refAttrType = new AttrType[SchemaType.ref.type,Long]
  implicit val stringAttrType = new AttrType[SchemaType.string.type,String]
  implicit val uriAttrType = new AttrType[SchemaType.uri.type,URI]
  implicit val uuidAttrType = new AttrType[SchemaType.uuid.type,UUID]
}