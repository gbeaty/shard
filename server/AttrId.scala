package sync.server

import sync._

import datomisca._

object AttrId {
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
}