package sync

import sync.server._
import datomisca._

import org.specs2._

class EntityChangeTests extends mutable.Specification {
  val db = new TestDb
  val id = new FinalId(1)

  "Updated" should {
    val fc0F = FactChange(Map(0 -> false))
    val fc0T = FactChange(Map(0 -> true))
    val fc1T = FactChange(Map(1 -> true))
    val fcAB = fc0F.merge(fc1T).get

    val fcsA = Map(0 -> fc0F, 2 -> fc0F, 3 -> fc0F)
    val fcsB = Map(0 -> fc0T, 1 -> fc1T, 3 -> fc1T)

    val uA = Updated(id, fcsA)
    val uB = Updated(id, fcsB)
    val uAB = uA.merge(uB).get.changes

    "take unilateral changes" in {
      uAB.get(1) ==== Some(fc1T)
      uAB.get(2) ==== Some(fc0F)
    }

    "merge bilateral changes" in {
      uAB.get(3) ==== Some(fcAB)
    }

    "remove canceled out changes" in {
      uAB.get(0) ==== None
    }

    "return None when all changes cancel" in {
      Updated(id, Map(0 -> fc0F)).merge(Updated(id, Map(0 -> fc0T))) ==== None
    }
  }
}