package shard.test

import shard._

import org.scalacheck._
import scala.math._
import java.util.{UUID, Date}
import java.net.URI

object Cols {
  implicit val bigdec = ColOf[BigDecimal]()
  implicit val bigint = ColOf[BigInt]()
  implicit val boolean = ColOf[Boolean]()
  implicit val bytes = ColOf[Array[Byte]]()
  implicit val double = ColOf[Double]()
  // implicit val float = ColOf[Float]()
  implicit val float = ColOf[Float]()
  implicit val instant = ColOf[Date]()
  // implicit val keyword = ColOf[clojure.lang.Keyword]()
  implicit val long = ColOf[Long]()
  // implicit val ref = ColOf[Ref]()
  implicit val string = ColOf[String]()
  // implicit val uri = ColOf[URI]()
  implicit val uuid = ColOf[UUID]()

  val all = bigdec :: bigint :: boolean :: bytes :: double :: float :: instant :: long :: string :: uuid :: CNil
}

object RowGen {
  implicit def arbUuid = Arbitrary(Gen.uuid)

  implicit val rowNilGen = Gen.const(Row[CNil.type](Map[Col,Any]()))
  implicit def rowNelGen[C <: CNel](implicit col: C#Head, headArb: Arbitrary[C#Head#Value], tailGen: Gen[Row[C#Tail]]) =
    tailGen.flatMap(tail => headArb.arbitrary.map(head => Row[C](tail.fields + (col -> head))))

  implicit val diffNilGen = Gen.const(Diff[CNil.type](Map[Col,Any]()))
  implicit def diffNelGen[C <: CNel]
    (implicit col: C#Head, headArb: Arbitrary[Option[C#Head#Value]], tailGen: Gen[Diff[C#Tail]]) =
      tailGen.flatMap { tail =>
        headArb.arbitrary.map { headOpt =>
          Diff[C](headOpt.map { head =>
            tail.diffs + (col -> head)
          }.getOrElse(tail.diffs))
        }
      }

  /*implicit val diffNilGen = Gen.const(Diff[CNil.type](Map[Int,Any]()))
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