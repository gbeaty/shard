package shard.test

import shard._

import org.scalacheck.Properties
import org.scalacheck.Prop.forAll
import org.scalacheck._

import java.util.{Date, UUID}
import java.net.URI
import java.math.{BigDecimal, BigInteger}

abstract class RowSpec[P <: PlatformOf[P], C <: Cols[P]]
  (val pName: String)(implicit val platform: P) extends Properties(pName + " rows") {

    implicit val arbRow: Arbitrary[Row[P,C]]

    property("diffs") = forAll { (last: Row[P,C], next: Row[P,C]) =>

      val forward = last.diff(next)
      val backward = next.diff(last)
      val nowhere = last.diff(last)
      forward(last) == next && backward(next) == last && nowhere(last) == last
    }
}

object ServerRowSpec extends RowSpec[shard.server.Platform,ServerTestCols.All]("server")(shard.server.platform) {
  import ServerRowGen._

  implicit lazy val arbRow = ServerRowGen.arbRow[ServerTestCols.All]
}

object JsRowSpec extends RowSpec[shard.js.Platform,JsTestCols.All]("js")(shard.js.platform) {
  import JsRowGen._

  implicit lazy val arbRow = JsRowGen.arbRow[JsTestCols.All]
}