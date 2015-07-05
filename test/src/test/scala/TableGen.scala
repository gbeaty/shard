package shard.test

import shard._
import shard.server._

import org.scalacheck._
import scala.math._
import java.util.{UUID, Date}
import java.net.URI

abstract class RowGen[P <: Platform](val platform: P) {
  import platform._

  implicit def arbUuid = Arbitrary(Gen.uuid)
  
  /*
  implicit val rowNilGen: Gen[Row[CNil.type]]
  implicit def rowNelGen[C <: Cols](implicit col: C#Head, headArb: Arbitrary[C#Head#Value], tailGen: Gen[Row[C#Tail]]):
    Gen[Row[C]]

  implicit val diffNilGen: Gen[Diff[CNil.type]]
  implicit def diffNelGen[C <: Cols]
    (implicit col: C#Head, headArb: Arbitrary[Option[C#Head#Value]], tailGen: Gen[Diff[C#Tail]]): Gen[Diff[C]]
  */

  implicit def arbRow[C <: Cols](implicit gen: Gen[Row[C]]) = Arbitrary.apply(gen)
  implicit def arbDiff[C <: Cols](implicit gen: Gen[Diff[C]]) = Arbitrary.apply(gen)
}

object ServerRowGen extends RowGen(shard.server.Platform) {
  import platform._

  implicit val rowNilGen = Gen.const(Row[CNil.type](Map[Col,Any]()))
  implicit def rowNelGen[C <: Cols]
    (implicit col: C#Head, headArb: Arbitrary[C#Type], tailGen: Gen[Row[C#Tail]]): Gen[Row[C]] =
      tailGen.flatMap(tail => headArb.arbitrary.map(head => Row[C](tail.fields + (col -> head))))

  implicit val diffNilGen = Gen.const(Diff[CNil.type](Map[Col,Any]()))
  implicit def diffNelGen[C <: Cols]
    (implicit col: C#Head, headArb: Arbitrary[Option[C#Head#Value]], tailGen: Gen[Diff[C#Tail]]): Gen[Diff[C]] =
      tailGen.flatMap { tail =>
        headArb.arbitrary.map { headOpt =>
          Diff[C](headOpt.map { head =>
            tail.diffs + (col -> head)
          }.getOrElse(tail.diffs))
        }
      }
}

// object JsRowGen extends RowGen(shard.js.Platform)