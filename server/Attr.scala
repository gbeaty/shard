package shard.server

import shard._

import datomisca._

object Attr {
  def fromId(attrId: Long)(implicit db: Database) = fromEntity(db.entity(attrId))

  def fromEntity(attr: datomisca.Entity) =
    (attr.getAs[Keyword](Attribute.ident), attr.getAs[String](Attribute.cardinality)) match {
      case (Some(ident), Some(card)) =>
        if(card == ":db.cardinality/one") 
          Some(new OneAttr[Any](ident.toString))
        else
          Some(new ManyAttr[Any](ident.toString))
      case _ => None
    }
}