package shard

import shard.server._
// import datomisca._

import org.specs2._

class EntityChangeTests extends mutable.Specification {
  val up = Upserted(0L)
  val one = toOneAttr(Schema.One.long)
  val many = toManyAttr(Schema.Many.long)

  "Upserted" should {

    "Be created empty" in {
      up.get(one) ==== None
      up.get(many) ==== None
    }

    val added = up.set(one)(1L, true)
    "Add single facts" in {      
      added.get(one) ==== Some(Some(1L))
      added.set(one)(0L, true).get(one) ==== Some(Some(0L))
    }

    val retracted = added.set(one)(1L, false)
    "Retract single facts" in {
      retracted.get(one) ==== Some(None)
      retracted.set(one)(1L, false).get(one) ==== Some(None)
      up.set(one)(1L, false).get(one) ==== Some(None)
    }

    val two = up.set(many)(1L, true).set(many)(2L, true)
    "Add many facts" in {
      two.get(many) ==== Some(Map(1L -> true, 2L -> true))
    }

    "Retract many facts" in {
      two.set(many)(1L, false).set(many)(3L, false).get(many) ==== Some(Map(1L -> false, 2L -> true, 3L -> false))
    }
  }
}