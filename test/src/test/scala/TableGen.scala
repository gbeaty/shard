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
  // val keyword = Col[Keyword]("keyword")
  val long = Col[Long]("long")
  // val ref = Col[Ref]("ref")
  val string = Col[String]("string")
  val uri = Col[URI]("uri")
  val uuid = Col[UUID]("uuid")

  val all = bigdec :: bigint :: boolean :: bytes :: double :: float :: instant :: long :: string :: uri :: uuid :: CNil
}

object RowGen {
  implicit val rnilGen = Gen.const(RNil)
  implicit def rnelGen[V,T <: Row](implicit headArb: Arbitrary[V], tailGen: Gen[T]) =
    tailGen.flatMap(tail =>  headArb.arbitrary.map(head => RNel(head, tail)))
}