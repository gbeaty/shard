package shard

import org.scalacheck._
import datomisca._

import java.util.Date
import java.net.URI
import java.util.UUID
import java.math.{BigDecimal, BigInteger}

// BigDecimal
// BigInt
// Boolean
// Array[Byte]
// Double
// Float
// Date
// Keyword
// Long
// Long
// String
// URI
// UUID

/*case class AttrVal[A <: Attr](attr: A, value: A#Value)

object AttrValGen {
  implicit val arbKeyword = Arbitrary(Arbitrary.arbString.arbitrary.map(s => clojure.lang.Keyword.intern("arb",s)))
  implicit val arbUri = Arbitrary(new URI("http://" + UUID.randomUUID.toString))
  implicit val arbUuid = Arbitrary(UUID.randomUUID)

  def apply[A <: Attr](attr: A)(implicit arb: Arbitrary[A#Value]) = arb.arbitrary//.map(AttrVal(attr, _))
}

object AttrDiffGen {
  def apply[A <: Attr](attr: A)(implicit arb: Arbitrary[A#Value]) = attr match {
    case attr: OneAttr[A#Value]  => one(attr)
    // case attr: ManyAttr[A#Value] => many(attr)
  }

  def one[A <: Attr](attr: A)(implicit arbValue: Arbitrary[A#Value]): Gen[Option[OneAttrDiff[A#Value]]] =
    implicitly[Arbitrary[Option[Boolean]]].arbitrary.flatMap { optAdd =>
      arbValue.arbitrary.map(v => optAdd.map(add => OneAttrDiff[A#Value](if(add) Some(v) else None)))
    }

  /*def many[A <: Attr](attr: A)(vs: Set[attr.Value])(implicit arbValue: Arbitrary[A#Value]): Gen[Option[ManyAttrDiff[A#Value]]] =
    vs.foldLeft*/
}

object UpsertGen {
  // def apply(id: Long)
}*/

/*object EntityChangeGen {

  implicit val arbKeyword = Arbitrary(Arbitrary.arbString.arbitrary.map(s => clojure.lang.Keyword.intern("arb",s)))
  implicit val arbUri = Arbitrary(new URI("http://" + UUID.randomUUID.toString))
  implicit val arbUuid = Arbitrary(UUID.randomUUID)

  def setAttrVal[A <: Attr]
    (up: Upserted, attr: A)
    (value: Gen[attr.Value], genOptAdd: Gen[Option[Boolean]]) =
      value.map { v =>
        genOptAdd.map { optAdd =>
          optAdd.map(up.set(attr)(v, _)).getOrElse(up)
        }
      }

  implicit class ArbAttrVal[V](val attr: AttrOf[V, _ <: AttrDiff])(implicit val arb: Arbitrary[V])

  def apply(id: Long, remove: Boolean) = {
    if(remove) {
      Removed(id)
    } else {
      /*val up = Upserted(id)
      def setOne[V](up: Upserted, attr: AttrOf[V,_ <: AttrDiff])(implicit arb: Arbitrary[V]) =
        setAttrVal(up, attr)(Arbitrary.arbitrary[V], Arbitrary.arbitrary[Option[Boolean]])

      def setOnes(up: Upserted, aavs: ArbAttrVal[_]*)*/
      
      import Schema.One._
      Seq[ArbAttrVal[_]](
        bigdec, bigint, boolean, bytes, double, float, instant, keyword, long, ref, string, uri, uuid
      ).foldLeft(Upserted(id)) { (up,aav) =>
        setAttrVal(up, aav.attr)(aav.arb.arbitrary, Arbitrary.arbitrary[Option[Boolean]])
      }
    }
  }
}*/