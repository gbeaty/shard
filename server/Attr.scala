package shard.server

import shard._

import datomisca._

object Attr {
  def fromId(attrId: Long)(implicit db: Database): Option[Attr] = fromEntity(db.entity(attrId))

  def fromEntity(attr: datomisca.Entity) =
    (attr.getAs[Keyword](Attribute.ident), attr.getAs[String](Attribute.cardinality)) match {
      case (Some(ident), Some(card)) =>
        if(card == ":db.cardinality/one") 
          Some(new OneAttr(ident))
        else
          Some(new ManyAttr(ident))
      case _ => None
    }
}
/*object AttrId {
  def apply(id: Int)(implicit db: Database) =
    if(db.entity(id).get(Attribute.cardinality) == Cardinality.one.keyword)
      OneAttrId[Any](id)
    else
      ManyAttrId[Any](id)

  def apply[DD <: AnyRef,C <: Cardinality,T]
    (attr: Attribute[DD,C])(implicit db: Database, r: Attribute2EntityReaderInj[DD,One,T]) = {
      val id = db.entity(attr.ident).id
      if(attr.cardinality == Cardinality.one)
        OneAttrId[T](id)
      else
        ManyAttrId[T](id)
      }
}*/