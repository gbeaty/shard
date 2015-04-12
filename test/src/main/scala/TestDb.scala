package sync

import datomisca._
import datomisca.executioncontext.ExecutionContextHelper._

import scala.concurrent._

class TestDb {
  val uri = "datomic:mem://" + java.util.UUID.randomUUID    

  import SchemaType._
  import Cardinality._
  val person = Namespace("person")
  val name = Attribute(person / "name", string, one)
  val age = Attribute(person / "age", long, one)
  val friends = Attribute(person / "friends", ref, many)

  val attributes = Set(name, age, friends)

  implicit val conn = {
    Datomic.createDatabase(uri)
    implicit val c = Datomic.connect(uri)
    Await.result(Datomic.transact(attributes), duration.Duration.Inf)    
    c
  }

  implicit def db = conn.database

  def apply(txs: Seq[TxData]) = Await.result(Datomic.transact(txs), duration.Duration.Inf)
}