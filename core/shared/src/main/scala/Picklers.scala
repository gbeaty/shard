package shard

import boopickle._

class Picklers[P <: Platform] {

  class RowPickler[H,T <: RList](implicit head: Pickler[H], tail: Pickler[T]) extends Pickler[RowOf[H,T]] {
    def pickle(row: shard.RowOf[H,T])(implicit state: boopickle.PickleState) {
      head.pickle(row.value)
      tail.pickle(row.tail)
    }
    def unpickle(implicit state: boopickle.UnpickleState) = RowOf(head.unpickle, tail.unpickle)
  }

  implicit def rnelPickler[H,T <: RList](implicit head: Pickler[H], tail: Pickler[T]) = new RowPickler[H,T]
  implicit val rnilPickler = new Pickler[RNil.type] {
    def pickle(rnil: RNil.type)(implicit state: boopickle.PickleState) = Unit
    def unpickle(implicit state: boopickle.UnpickleState) = RNil
  }

  import BasicPicklers._
  class StringBasedPickler[A](fromString: String => A) extends Pickler[A] {
    def pickle(obj: A)(implicit state: PickleState) = StringPickler.pickle(obj.toString)
    def unpickle(implicit state: UnpickleState) = fromString(StringPickler.unpickle)
  }
  object BigDecimalPickler extends StringBasedPickler[BigDecimal](BigDecimal.apply _)
  object BigIntPickler extends StringBasedPickler[BigInt](BigInt.apply _)
}