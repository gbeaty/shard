package shard

import datomisca._

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

  implicit def toAttr[DD <: AnyRef,C <: datomisca.Cardinality,T]
    (attr: datomisca.Attribute[DD,C])
    (implicit r: Attribute2EntityReaderInj[DD,One,T]) =
      if(attr.cardinality == datomisca.Cardinality.one)
        new OneAttr[T](attr.ident.toString)
      else
        new ManyAttr[T](attr.ident.toString)
}