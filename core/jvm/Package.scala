package shard

import datomisca._
import boopickle._
import java.util.Date
import java.net.URI
import java.util.UUID
import scala.reflect._

package object server extends shard.Platform {
  val platform = this

  trait Col {
    type Value
    def apply(entity: Entity): Option[Value]
  }

  type UnderlyingRow = Array[Any]
  type UnderlyingDiff = Array[Option[Any]]

  def newArray[A](len: Int)(implicit ct: ClassTag[A]) = new Array[A](len)

  type Rows[A] = Seq[A]

  case class AttributeCol[V,DD <: AnyRef]
  (attribute: Attribute[DD,Cardinality.one.type])
  (implicit attrC: Attribute2EntityReaderInj[DD,Cardinality.one.type,V]) extends Col {
    type Value = V
    def apply(entity: Entity) = entity.get(attribute)
  }
}