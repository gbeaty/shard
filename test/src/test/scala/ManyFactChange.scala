package sync

import org.specs2._

class FactChangeTests extends mutable.Specification {

  val vs = Map(
    1 -> (None,         Some(false)),
    2 -> (None,         Some(true)),
    3 -> (Some(false),  None),
    4 -> (Some(false),  Some(false)),
    5 -> (Some(false),  Some(true)),
    6 -> (Some(true),   None),
    7 -> (Some(true),   Some(false)),
    8 -> (Some(true),   Some(true))
  )
  val as = vs.flatMap(kv => kv._2._1.map(v => (kv._1 -> v)))
  val bs = vs.flatMap(kv => kv._2._2.map(v => (kv._1 -> v)))
  
  "merge" should {
    val merged = FactChange(as).merge(FactChange(bs)).get.v
    "cancel out values both asserted and retracted" in {
      merged.get(5) ==== None
      merged.get(7) ==== None
    }
    "assert" in {
      Seq(2, 6, 8).map(merged(_)) == Seq(true, true, true)
    }
    "retract" in {
      Seq(1, 3, 4).map(merged(_)) == Seq(false, false, false) 
    }
    "contain no other values" in {
      merged.keySet.min ==== 1
      merged.keySet.max ==== 8
    }
    "return None when all values cancel" in {
      FactChange(Map(1 -> false)).merge(FactChange(Map(1 -> true))) ==== None
    }
  }
}