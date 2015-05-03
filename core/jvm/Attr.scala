package shard.server

import shard._

import boopickle._
import datomisca._

class AttrType[DD,Value](implicit val pickler: Pickler[Value])

object Attr {
  def fromId(attrId: Long)(implicit db: Database) = fromEntity(db.entity(attrId))

  def fromEntity(attr: datomisca.Entity): Option[Attr] =
    (attr.getAs[Keyword](Attribute.ident), attr.getAs[Keyword](Attribute.valueType), attr.getAs[String](Attribute.cardinality)) match {
      case (Some(ident), Some(valueType), Some(card)) => {
        (valueType.toString match {
          case ":db.type/keyword" => Some(Picklers.keyword)
          case ":db.type/string" => Some(implicitly[Pickler[String]])
          case ":db.type/boolean" => Some(implicitly[Pickler[Boolean]])
          case ":db.type/long" => Some(implicitly[Pickler[Long]])
          case ":db.type/bigint" => Some(Picklers.bigInt)
          case ":db.type/float" => Some(implicitly[Pickler[Float]])
          case ":db.type/double" => Some(implicitly[Pickler[Double]])
          case ":db.type/bigdec" => Some(Picklers.bigDec)
          case ":db.type/ref" => Some(implicitly[Pickler[Long]])
          case ":db.type/instant" => Some(Picklers.date)
          case ":db.type/uuid" => Some(Picklers.uri)
          case ":db.type/uri" => Some(implicitly[Pickler[java.util.UUID]])
          case ":db.type/bytes" => Some(implicitly[Pickler[Array[Byte]]])
          case _ => None
        }).map { p =>
          if(card == ":db.cardinality/one")
            new OneAttr(ident.toString)(p)
          else
            new ManyAttr(ident.toString)(p)
        }
      }
      case _ => None
    }
}