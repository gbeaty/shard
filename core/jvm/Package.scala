package shard

import datomisca._
import boopickle._
import java.util.Date
import java.net.URI
import java.util.UUID

package object server extends Picklers {
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

  /*implicit def toAttr[DD,C <: Cardinality,Value]
    (attr: Attribute[DD,C])(implicit at: AttrType[DD,Value]) =
      if(attr.cardinality == Cardinality.one)
        new OneAttr[Value](attr.ident.toString)
      else
        new ManyAttr[Value](attr.ident.toString)*/
}