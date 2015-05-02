package shard.server

import shard._

import boopickle._
import datomisca._

class AttrType[DD,Value](implicit val pickler: Pickler[Value])

object Attr {
  def fromId[V](attrId: Long)(implicit db: Database) = fromEntity[V](db.entity(attrId))

  def fromEntity[V](attr: datomisca.Entity): Option[Attr { type Value = V }] =
    (attr.getAs[Keyword](Attribute.ident), attr.getAs[String](Attribute.cardinality)) match {
      case (Some(ident), Some(card)) =>
        if(card == ":db.cardinality/one") 
          Some(new OneAttr[V](ident.toString))
        else
          Some(new ManyAttr[V](ident.toString))
      case _ => None
    }
}