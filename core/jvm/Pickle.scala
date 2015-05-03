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
  implicit val bigDec = toStringPickler[BigDecimal]
  implicit val bigInt = toStringPickler[BigInt]
  implicit val keyword = toStringPickler[Keyword]
  implicit val uri = toStringPickler[URI]
  implicit val date = new Pickler[Date] {
    def pickle(obj: Date)(implicit state: PickleState) = Pickler.LongPickler.pickle(obj.getTime)
  }
}

trait Picklers {
  /*import Picklers._
  implicit val bigDec = new AttrType[SchemaType.bigdec.type,BigDecimal]
  implicit val bigInt = new AttrType[SchemaType.bigint.type,BigInt]
  implicit val boolean = new AttrType[SchemaType.boolean.type,Boolean]
  implicit val bytes = new AttrType[SchemaType.bytes.type,Array[Byte]]
  implicit val double = new AttrType[SchemaType.double.type,Double]
  implicit val float = new AttrType[SchemaType.float.type,Float]
  implicit val instant = new AttrType[SchemaType.instant.type,Date]
  implicit val keyword = new AttrType[SchemaType.keyword.type,Keyword]
  implicit val long = new AttrType[SchemaType.long.type,Long]
  implicit val ref = new AttrType[SchemaType.ref.type,Long]
  implicit val string = new AttrType[SchemaType.string.type,String]
  implicit val uri = new AttrType[SchemaType.uri.type,URI]
  implicit val uuid = new AttrType[SchemaType.uuid.type,UUID]*/
}