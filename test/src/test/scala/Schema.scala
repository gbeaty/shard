package shard.test

import shard._
import shard.server._
import datomisca._

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

object TestCols {
  implicit val bigdec = AttributeCol(Schema.bigdec)
  implicit val bigint = AttributeCol(Schema.bigint)
  implicit val boolean = AttributeCol(Schema.boolean)
  implicit val bytes = AttributeCol(Schema.bytes)
  implicit val double = AttributeCol(Schema.double)
  implicit val float = AttributeCol(Schema.float)
  implicit val instant = AttributeCol(Schema.instant)
  implicit val keyword = AttributeCol(Schema.keyword)
  implicit val long = AttributeCol(Schema.long)
  implicit val ref = AttributeCol(Schema.ref)
  implicit val string = AttributeCol(Schema.string)
  implicit val uri = AttributeCol(Schema.uri)
  implicit val uuid = AttributeCol(Schema.uuid)

  import shard.server.Platform._

  val all = bigdec :: bigint :: boolean :: bytes :: double :: float :: instant :: long :: string :: uuid :: CNil
}