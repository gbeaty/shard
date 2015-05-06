package shard

import datomisca._
import datomisca.executioncontext.ExecutionContextHelper._

import scala.concurrent._

class Attrs[C <: Cardinality](ns: Namespace, prefix: String, card: C) {
  val bigdec = Attribute(ns / (prefix + "Bigdec"), SchemaType.bigdec, card)
  val bigint = Attribute(ns / (prefix + "Bigint"), SchemaType.bigint, card)
  val boolean = Attribute(ns / (prefix + "Boolean"), SchemaType.boolean, card)
  val bytes = Attribute(ns / (prefix + "Bytes"), SchemaType.bytes, card)
  val double = Attribute(ns / (prefix + "Double"), SchemaType.double, card)
  val float = Attribute(ns / (prefix + "Float"), SchemaType.float, card)
  val instant = Attribute(ns / (prefix + "Instant"), SchemaType.instant, card)
  val keyword = Attribute(ns / (prefix + "Keyword"), SchemaType.keyword, card)
  val long = Attribute(ns / (prefix + "Long"), SchemaType.long, card)
  val ref = Attribute(ns / (prefix + "Ref"), SchemaType.ref, card)
  val string = Attribute(ns / (prefix + "String"), SchemaType.string, card)
  val uri = Attribute(ns / (prefix + "Uri"), SchemaType.uri, card)
  val uuid = Attribute(ns / (prefix + "Uuid"), SchemaType.uuid, card)

  val all = Set(bigdec, bigint, boolean, bytes, double, float, instant, keyword, long, ref, string, uri, uuid)
}

object Schema {
  import SchemaType._
  import Cardinality._
  val attrs = Namespace("attrs")

  object One extends Attrs(attrs, "one", Cardinality.one)
  object Many extends Attrs(attrs, "many", Cardinality.many)
}

class TestDb {
  val uri = "datomic:mem://" + java.util.UUID.randomUUID

  implicit val conn = {
    Datomic.createDatabase(uri)
    implicit val c = Datomic.connect(uri)
    Await.result(Datomic.transact(Schema.One.all ++ Schema.Many.all), duration.Duration.Inf)
    c
  }

  implicit def db = conn.database

  def apply(txs: Seq[TxData]) = Await.result(Datomic.transact(txs), duration.Duration.Inf)
}