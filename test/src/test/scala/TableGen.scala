package shard.test

import shard._

import org.scalacheck._
import scala.math._
import java.util.{UUID, Date}
import java.net.URI

abstract class RowGen[P <: Platform](val platform: P) {
  import platform._

  implicit def arbUuid = Arbitrary(Gen.uuid)

  implicit def arbRow[C <: Cols](implicit gen: Gen[Row[C]]) = Arbitrary.apply(gen)
  implicit def arbDiff[C <: Cols](implicit gen: Gen[Diff[C]]) = Arbitrary.apply(gen)

  case class RowTemp[C <: CList](fields: Seq[Any])
  case class DiffTemp[C <: CList](fields: Seq[Option[Any]])

  implicit val rowTempNilGen = Gen.const(RowTemp[CNil.type](Seq[Any]()))
  implicit def rowTempNelGen[C <: Cols]
    (implicit headArb: Arbitrary[C#Type], tailGen: Gen[RowTemp[C#Tail]]) =
      tailGen.flatMap(tail => headArb.arbitrary.map(head => RowTemp[C](head +: tail.fields)))

  implicit val diffTempNilGen = Seq[Any]()
  implicit def diffTempNelGen[C <: Cols]
    (implicit headArb: Arbitrary[Option[C#Type]], tailGen: Gen[DiffTemp[C#Tail]]) =
      tailGen.flatMap(tail => headArb.arbitrary.map(head => DiffTemp[C](head +: tail.fields)))

  implicit def rowGen[C <: Cols](implicit tempGen: Gen[RowTemp[C]]) =
    tempGen.map(t => Row[C](t.fields.toArray: Array[Any]))
  
  implicit def diffGen[C <: Cols](implicit tempGen: Gen[DiffTemp[C]]) =
    tempGen.map(t => Diff[C](t.fields.toArray: Array[Option[Any]]))
}

object ServerRowGen extends RowGen(shard.server.platform)

object JsRowGen extends RowGen(shard.js.platform)