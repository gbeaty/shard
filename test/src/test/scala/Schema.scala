package shard.test

import shard._
import datomisca._

import java.util.{Date, UUID}
import java.net.URI
import scala.math.BigDecimal

import boopickle.DefaultBasic._

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

abstract class TestCols[P <: Platform](val platform: P) {
  import platform._

  val bigdec: ColOf[BigDecimal]
  val bigint: ColOf[BigInt]
  val boolean: ColOf[Boolean]
  val bytes: ColOf[Array[Byte]]
  val double: ColOf[Double]
  val float: ColOf[Float]
  val instant: ColOf[Date]
  val keyword: ColOf[Keyword]
  val long: ColOf[Long]
  // val ref: ColOf[Long]
  val string: ColOf[String]
  val uri: ColOf[URI]
  val uuid: ColOf[UUID]

  val all =
    bigdec :: bigint :: boolean :: bytes :: double :: float ::
    instant :: long :: string :: uuid :: platform.CNil
}

object ServerTestCols extends TestCols(shard.server.platform) {
  import platform._

  val bigdec = AttributeCol(Schema.bigdec)
  val bigint = AttributeCol(Schema.bigint)
  val boolean = AttributeCol(Schema.boolean)
  val bytes = AttributeCol(Schema.bytes)
  val double = AttributeCol(Schema.double)
  val float = AttributeCol(Schema.float)
  val instant = AttributeCol(Schema.instant)
  val keyword = AttributeCol(Schema.keyword)
  val long = AttributeCol(Schema.long)
  // val ref = AttributeCol(Schema.ref)
  val string = AttributeCol(Schema.string)
  val uri = AttributeCol(Schema.uri)
  val uuid = AttributeCol(Schema.uuid)
}