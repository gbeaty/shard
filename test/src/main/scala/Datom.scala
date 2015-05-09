package shard

import java.{lang => jl}

case class MockDatom(a: jl.Integer, added: Boolean, e: jl.Long, tx: jl.Long, v: Object) extends datomic.Datom {
  def get(index: Int): Object = index match {
    case 0 => e
    case 1 => a
    case 2 => v
    case 3 => tx
    case 4 => added.asInstanceOf[Object]
    case _ => null
  }
}
object Datom {
  def apply(a: Int, added: Boolean, e: Long, tx: Long, v: AnyRef) =
    new datomisca.Datom(MockDatom(a, added, e, tx, v))
}