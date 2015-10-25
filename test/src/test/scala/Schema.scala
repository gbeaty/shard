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

object TestCols {
  import shard.server._

  val bigdec = Col(Schema.bigdec)
  val bigint = Col(Schema.bigint)
  val boolean = Col(Schema.boolean)
  val bytes = Col(Schema.bytes)
  val double = Col(Schema.double)
  val float = Col(Schema.float)
  val instant = Col(Schema.instant)
  val keyword = Col(Schema.keyword)
  val long = Col(Schema.long)
  // val ref = Col(Schema.ref)
  val string = Col(Schema.string)
  val uri = Col(Schema.uri)
  val uuid = Col(Schema.uuid)

  val all =
    bigdec :: bigint :: boolean :: bytes :: double :: float ::
    instant :: long :: string :: uuid :: CNil
}