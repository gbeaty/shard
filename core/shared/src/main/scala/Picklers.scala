package shard

import boopickle._

/*abstract class Picklers[P <: Platform](val platform: P) {
  trait PList[C <: platform.CList] {
    val length: Int
  }
  class PNel[C <: platform.Cols](val pickler: Pickler[C#Head], val tail: PList[C#Tail])
    extends PList[C] {
      val length = tail.length + 1
    }

  implicit object PNil extends PList[platform.CNil.type] {
    val length = 0
  }

  implicit def cnelPickler[C <: platform.Cols](implicit head: Pickler[C#Head], tail: PList[C#Tail]) =
    new PNel(head, tail)

  def pickleRow[C <: platform.Cols](pickler: RowPickler[C], rowData: platform.RowData)(implicit state: PickleState): Unit
  def unpickleRow[C <: platform.Cols](pickler: RowPickler[C])(implicit state: UnpickleState): platform.RowData

  class RowPickler[C <: platform.Cols](val picklers: Array[Pickler[_]]) extends Pickler[platform.Row[C]] {
    def pickle(row: platform.Row[C])(implicit state: PickleState) = pickleRow(this, row.data)
    def unpickle(implicit state: UnpickleState) = platform.Row[C](unpickleRow(this))
  }

  implicit def rowPicklerGen[C <: platform.Cols](implicit pnel: PNel[C]) = {
    val res = new Array[Pickler[_]](pnel.length)
    def buildArray(plist: PList[_], index: Int): Array[Pickler[_]] = plist match {
      case PNil => res
      case pl: PNel[_] => {
        res(index) = pl.pickler
        buildArray(pl.tail, index + 1)
      }
    }
    new RowPickler(buildArray(pnel, 0))
  }
}*/

abstract class Picklers[P <: Platform](val platform: P) {

  def emptyRow(len: Int): platform.RowData
  def pickleRowData
    [C <: platform.Cols]
    (rowPickler: PNel[C], row: platform.RowData, index: Int)(implicit state: PickleState): Unit

  def unpickleRowData[C <: platform.Cols]
    (rowPickler: PNel[C], result: platform.RowData, index: Int)(implicit state: UnpickleState): platform.RowData

  trait PList[C <: platform.CList] {
    val length: Int

    // def pickle(row: platform.Row[C])(implicit state: PickleState) = pickleRowData(this, row.data, 0)
    // def unpickle(implicit state: UnpickleState) = platform.Row[C](unpickleRowData(this, emptyRow(length), 0))
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

  import BasicPicklers._
  class StringBasedPickler[A](fromString: String => A) extends Pickler[A] {
    def pickle(obj: A)(implicit state: PickleState) = StringPickler.pickle(obj.toString)
    def unpickle(implicit state: UnpickleState) = fromString(StringPickler.unpickle)
  }
  object BigDecimalPickler extends StringBasedPickler[BigDecimal](BigDecimal.apply _)
  object BigIntPickler extends StringBasedPickler[BigInt](BigInt.apply _)
}