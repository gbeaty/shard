package shard

import org.scalacheck._
import scala.math._
import java.util.{UUID, Date}
import java.net.URI

object Cols {
  val bigdec = ColOf[BigDecimal]()
  val bigint = ColOf[BigInt]()
  val boolean = ColOf[Boolean]()
  val bytes = ColOf[Array[Byte]]()
  val double = ColOf[Double]()
  // val float = ColOf[Float]()
  object float extends ColOf[Double]()
  val instant = ColOf[Date]()
  // val keyword = ColOf[clojure.lang.Keyword]()
  val long = ColOf[Long]()
  // val ref = ColOf[Ref]()
  val string = ColOf[String]()
  // val uri = ColOf[URI]()
  val uuid = ColOf[UUID]()

  val all = bigdec :: bigint :: boolean :: bytes :: double :: float :: instant :: long :: string :: uuid :: CNil
}

object RowGen {
  implicit def arbUuid = Arbitrary(Gen.uuid)

  /*implicit val rowNilGen = Gen.const(Row[CNil.type](Array[Any]()))
  implicit def rowNelGen[C <: CNel](implicit headArb: Arbitrary[C#Head], tailGen: Gen[Row[C#Tail]]) =
    tailGen.flatMap(tail => headArb.arbitrary.map(head => Row[C](tail.values :+ head)))

  implicit val diffNilGen = Gen.const(Diff[CNil.type](Map[Int,Any]()))
  implicit def diffNelGen[C <: CNel](implicit headArb: Arbitrary[Option[C#Head]], tailGen: Gen[Row[C#Tail]]) =
    tailGen.flatMap(tail => headArb.arbitrary.map(head => Row[C](tail.diffs :+ head)))*/

  /*implicit val rnilGen = Gen.const(RNil)
  implicit def rnelGen[V,T <: Row](implicit headArb: Arbitrary[V], tailGen: Gen[T]) =
    tailGen.flatMap(tail =>  headArb.arbitrary.map(head => RNel(head, tail)))

  implicit val dnilGen = Gen.const(DNil)
  implicit def dnelGen[V,T <: Diff](implicit headArb: Arbitrary[Option[V]], tailGen: Gen[T]) =
    tailGen.flatMap(tail =>  headArb.arbitrary.map(head => DNel(head, tail)))*/

  implicit def arbRow[C <: Columns](implicit gen: Gen[Row[C]]) = Arbitrary.apply(gen)
  implicit def arbDiff[C <: Columns](implicit gen: Gen[Diff[C]]) = Arbitrary.apply(gen)
}