package shard

import boopickle._

class Picklers[P <: Platform](val platform: P) {

  sealed trait PList[C <: platform.CList] {
    val length: Int
  }
  class PNel[C <: platform.Cols](val pickler: Pickler[C#Head], val tail: PList[C#Tail])
    extends PList[C] {
      type Head = C#Head
      val length = tail.length + 1
    }
  implicit object PNil extends PList[platform.CNil.type] {
    val length = 0
  }

  implicit def cnelPickler[C <: platform.Cols](implicit head: Pickler[C#Head], tail: PList[C#Tail]) =
    new PNel(head, tail)

  class RowPickler[C <: platform.Cols](implicit plist: PNel[C]) {
    
    def pickle(row: platform.Row[C])(implicit state: PickleState) = {
      var i = 0
      var pHead: PList[_] = plist
      while(i < row.data.length) {
        pHead match {
          case pnel: PNel[_] => {
            pnel.pickler.pickle(row(i).asInstanceOf[pnel.Head])
            pHead = pnel.tail
          }
          case PNil => Unit          
        }        
      }
    }

    def unpickle(implicit state: UnpickleState) = {
      var i = 0
      var pHead: PList[_] = plist
      val res = Array[Any]()
      while(i < plist.length) {
        pHead match {
          case pnel: PNel[_] => {
            res(i) = pnel.pickler.unpickle
            pHead = pnel.tail
          }
          case PNil =>
        }
      }
      platform.Row[C](res)
    }
  }

  import BasicPicklers._
  class StringBasedPickler[A](fromString: String => A) extends Pickler[A] {
    def pickle(obj: A)(implicit state: PickleState) = StringPickler.pickle(obj.toString)
    def unpickle(implicit state: UnpickleState) = fromString(StringPickler.unpickle)
  }
  object BigDecimalPickler extends StringBasedPickler[BigDecimal](BigDecimal.apply _)
  object BigIntPickler extends StringBasedPickler[BigInt](BigInt.apply _)
}