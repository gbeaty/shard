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

abstract class TestCols[P <: PlatformOf[P]](val platform: P) {
  import platform._

  val bigdec: P#Col { type Value = BigDecimal }
  val bigint: P#Col { type Value = BigInt }
  val boolean: P#Col { type Value = Boolean }
  val bytes: P#Col { type Value = Array[Byte] }
  val double: P#Col { type Value = Double }
  val float: P#Col { type Value = Float }
  val instant: P#Col { type Value = Date }
  val keyword: P#Col { type Value = Keyword }
  val long: P#Col { type Value = Long }
  // val ref: P#Col { type Value = Long }
  val string: P#Col { type Value = String }
  val uri: P#Col { type Value = URI }
  val uuid: P#Col { type Value = UUID }

  val all =
    bigdec :: bigint :: boolean :: bytes :: double :: float ::
    instant :: long :: string :: uuid :: CNil
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

object JsTestCols extends TestCols(shard.js.platform) {
  import platform._

  val bigdec = Column[BigDecimal]
  val bigint = Column[BigInt]
  val boolean = Column[Boolean]
  val bytes = Column[Array[Byte]]
  val double = Column[Double]
  val float = Column[Float]
  val instant = Column[Date]
  val keyword = Column[Keyword]
  val long = Column[Long]
  // val ref = Column[Long]
  val string = Column[String]
  val uri = Column[URI]
  val uuid = Column[UUID]
}