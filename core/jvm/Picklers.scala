package shard.server

import shard._
import boopickle._
import datomisca._
import java.util.Date
import java.net.URI
import java.util.UUID

import scala.math._

/*object Picklers extends shard.Picklers(platform) {
  def pickleRow[C <: platform.Cols](pickler: RowPickler[C], rowData: platform.RowData)(implicit state: PickleState) {
    val length = pickler.picklers.length
    var i = 0
    while(i < length) {
      val p = pickler.picklers(i)
      p.pickle(rowData(i))
    }
  }
  def unpickleRow[C <: platform.Cols](pickler: RowPickler[C])(implicit state: UnpickleState) = {
    val length = pickler.picklers.length
    var i = 0
    val res = new Array[Any](length)
    while(i < length) {
      pickler.picklers(i).unpickle
    }
    res
  }
}*/

object Picklers extends shard.Picklers(platform) {
  def emptyRow(len: Int) = Array[Any](len)

  def pickleRowData
    [C <: platform.Cols]
    (rowPickler: PNel[C], row: platform.RowData, index: Int)(implicit state: PickleState) = {
      rowPickler.pickler.pickle(row(index).asInstanceOf[rowPickler.Head])
    }

  def unpickleRowData[C <: platform.Cols]
    (rowPickler: PNel[C], result: platform.RowData, index: Int)(implicit state: UnpickleState): platform.RowData = ???
    
  /*def unpickleRowData[C <: platform.CList]
    (rowPickler: RowPickler[C], result: platform.RowData, index: Int)(implicit state: UnpickleState) =
      if(rowPickler.length == 0) {
        result
      } else {
        result(index) = rowPickler.unpickle
        unpickeRowData(rowPickler.tail, result, index + 1)
      }*/
}

/*object Picklers {
  import BasicPicklers._

  class StringBasedPickler[A](fromString: String => A) extends Pickler[A] {
    def pickle(obj: A)(implicit state: PickleState) = StringPickler.pickle(obj.toString)
    def unpickle(implicit state: UnpickleState) = fromString(StringPickler.unpickle)
  }
  object BigDecimalPickler extends StringBasedPickler[BigDecimal](BigDecimal.apply _)
  object BigIntPickler extends StringBasedPickler[BigInt](BigInt.apply _)

  trait RowPickler[C <: CList] {
    val length: Int

    def pickle(row: platform.Row[C])(implicit state: PickleState) = pickleData(row.data, 0)
    def unpickle(implicit state: UnpickleState) = Row[C](unpickleData(Array[Any](length), 0))

    def pickleData(row: platform.RowData, index: Int)(implicit state: PickleState): Unit
    def unpickleData(result: Array[Any], index: Int)(implicit state: UnpickleState): Array[Any]
  }
  class CNelPickler[C <: Cols](val pickler: Pickler[C#Head], val tail: RowPickler[C#Tail])
    extends RowPickler[C] {
      val length = tail.length + 1

      def pickleData(row: platform.RowData, index: Int)(implicit state: PickleState) {
        pickler.pickle(row(index).asInstanceOf[C#Head])
        tail.pickleData(row, index + 1)
      }
      def unpickleData(result: Array[Any], index: Int)(implicit state: UnpickleState) = {
        result(index) = pickler.unpickle
        tail.unpickleData(result, index + 1)
      }
    }
  implicit object CNilPickler extends RowPickler[CNil.type] {
    val length = 0

    def pickleData(row: platform.RowData, index: Int)(implicit state: PickleState) {}
    def unpickleData(result: Array[Any], index: Int)(implicit state: UnpickleState) = result
  }

  implicit def cnelPickler[C <: Cols](implicit head: Pickler[C#Head], tail: RowPickler[C#Tail]) =
    new CNelPickler(head, tail)
}*/