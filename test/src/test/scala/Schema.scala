package shard.test

import shard._
import datomisca._

import java.util.{Date, UUID}
import java.net.URI
import scala.math.BigDecimal

object Schema {
  val ns = Namespace("test")
  val bigdec = Attribute(ns / "Bigdec", SchemaType.bigdec, Cardinality.one)
  val bigint = Attribute(ns / "Bigint", SchemaType.bigint, Cardinality.one)
  val boolean = Attribute(ns / "Boolean", SchemaType.boolean, Cardinality.one)
  val bytes = Attribute(ns / "Bytes", SchemaType.bytes, Cardinality.one)
  val double = Attribute(ns / "Double", SchemaType.double, Cardinality.one)
  val float = Attribute(ns / "Float", SchemaType.float, Cardinality.one)
  val instant = Attribute(ns / "Instant", SchemaType.instant, Cardinality.one)
  val keyword = Attribute(ns / "Keyword", SchemaType.keyword, Cardinality.one)
  val long = Attribute(ns / "Long", SchemaType.long, Cardinality.one)
  val ref = Attribute(ns / "Ref", SchemaType.ref, Cardinality.one)
  val string = Attribute(ns / "String", SchemaType.string, Cardinality.one)
  val uri = Attribute(ns / "Uri", SchemaType.uri, Cardinality.one)
  val uuid = Attribute(ns / "Uuid", SchemaType.uuid, Cardinality.one)
}

trait TestCols {
  implicit val bigdec: ColOf[BigDecimal]
  implicit val bigint: ColOf[BigInt]
  implicit val boolean: ColOf[Boolean]
  implicit val bytes: ColOf[Array[Byte]]
  implicit val double: ColOf[Double]
  implicit val float: ColOf[Float]
  implicit val instant: ColOf[Date]
  implicit val keyword: ColOf[Keyword]
  implicit val long: ColOf[Long]
  // implicit val ref: ColOf[Long]
  implicit val string: ColOf[String]
  implicit val uri: ColOf[URI]
  implicit val uuid: ColOf[UUID]
}

object ServerTestCols extends TestCols {
  import shard.server.AttributeCol  

  implicit val bigdec = AttributeCol(Schema.bigdec)
  implicit val bigint = AttributeCol(Schema.bigint)
  implicit val boolean = AttributeCol(Schema.boolean)
  implicit val bytes = AttributeCol(Schema.bytes)
  implicit val double = AttributeCol(Schema.double)
  implicit val float = AttributeCol(Schema.float)
  implicit val instant = AttributeCol(Schema.instant)
  implicit val keyword = AttributeCol(Schema.keyword)
  implicit val long = AttributeCol(Schema.long)
  // implicit val ref = AttributeCol(Schema.ref)
  implicit val string = AttributeCol(Schema.string)
  implicit val uri = AttributeCol(Schema.uri)
  implicit val uuid = AttributeCol(Schema.uuid)

  val all = bigdec :: bigint :: boolean :: bytes :: double :: float :: instant :: keyword :: long :: string :: uri :: uuid :: CNil
}