package shard.server

import shard._
import datomisca._

trait Binding {
  def apply(to: datomisca.Entity): Boolean
}
object Binding {
  trait Attr[DD <: AnyRef,A] extends Binding {  
    val attr: Attribute[DD,Cardinality.one.type]
    implicit val reader: Attribute2EntityReaderInj[DD,Cardinality.one.type,A]
    def bindsValue(ov: Option[A]): Boolean

    val keyword = clojure.lang.Keyword.intern(attr.toString)
    def apply(to: datomisca.Entity) = bindsValue(to.get(attr))
  }
  case class Static[DD <: AnyRef,A]
    (attr: Attribute[DD,Cardinality.one.type], value: Option[A])
    (implicit val reader: Attribute2EntityReaderInj[DD,Cardinality.one.type,A]) extends Attr[DD,A] {
      def bindsValue(ov: Option[A]) = ov == this.value
  }
  trait Comparison[DD <: AnyRef,A] extends Attr[DD,A] {
    val compare: (A, A) => Boolean
    val value: A

    def bindsValue(ov: Option[A]) = ov.exists(v => compare(v, value))
  }
  case class Gt[DD <: AnyRef,A](attr: Attribute[DD,Cardinality.one.type], value: A)
    (implicit n: Numeric[A], val reader: Attribute2EntityReaderInj[DD,Cardinality.one.type,A]) extends Comparison[DD,A] {
      val compare = n.gt _
  }
  case class Gteq[DD <: AnyRef,A](attr: Attribute[DD,Cardinality.one.type], value: A)
    (implicit n: Numeric[A], val reader: Attribute2EntityReaderInj[DD,Cardinality.one.type,A]) extends Comparison[DD,A] {
      val compare = n.gteq _
  }
  case class Lt[DD <: AnyRef,A](attr: Attribute[DD,Cardinality.one.type], value: A)
    (implicit n: Numeric[A], val reader: Attribute2EntityReaderInj[DD,Cardinality.one.type,A]) extends Comparison[DD,A] {
      val compare = n.lt _
  }
  case class Lteq[DD <: AnyRef,A](attr: Attribute[DD,Cardinality.one.type], value: A)
    (implicit n: Numeric[A], val reader: Attribute2EntityReaderInj[DD,Cardinality.one.type,A]) extends Comparison[DD,A] {
      val compare = n.lteq _
  }
  case class Or(bindings: Binding*) extends Binding {
    def apply(to: datomisca.Entity) = bindings.exists(_.apply(to))
  }
}