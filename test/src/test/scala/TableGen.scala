package shard

import org.scalacheck._
import scala.math._
import java.util.{UUID, Date}
import java.net.URI

object Cols {
  val bigdec = Col[BigDecimal]("bigdec")
  val bigint = Col[BigInt]("bigint")
  val boolean = Col[Boolean]("boolean")
  val bytes = Col[Array[Byte]]("bytes")
  val double = Col[Double]("double")
  val float = Col[Float]("float")
  val instant = Col[Date]("instant")
  // val keyword = Col[clojure.lang.Keyword]("keyword")
  val long = Col[Long]("long")
  // val ref = Col[Ref]("ref")
  val string = Col[String]("string")
  // val uri = Col[URI]("uri")
  val uuid = Col[UUID]("uuid")

  val all = bigdec :: bigint :: boolean :: bytes :: double :: float :: instant :: long :: string :: uuid :: CNil
}

object RowGen {
  implicit def arbUuid = Arbitrary(Gen.uuid)

  implicit val rnilGen = Gen.const(RNil)
  implicit def rnelGen[V,T <: Row](implicit headArb: Arbitrary[V], tailGen: Gen[T]) =
    tailGen.flatMap(tail =>  headArb.arbitrary.map(head => RNel(head, tail)))

  implicit val dnilGen = Gen.const(DNil)
  implicit def dnelGen[V,T <: Diff](implicit headArb: Arbitrary[Option[V]], tailGen: Gen[T]) =
    tailGen.flatMap(tail =>  headArb.arbitrary.map(head => DNel(head, tail)))

  implicit def arbRow[R <: Row](implicit gen: Gen[R]) = Arbitrary.apply(gen)
  implicit def arbDiff[D <: Diff](implicit gen: Gen[D]) = Arbitrary.apply(gen)
}