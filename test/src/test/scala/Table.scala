package shard

import org.scalacheck.Properties
import org.scalacheck.Prop.forAll

import java.util.{Date, UUID}
import java.net.URI
import java.math.{BigDecimal, BigInteger}

object RowSpec extends Properties("Changeset") {
  import RowGen._

  property("rowDiffs") = forAll { (last: Cols.all.RowOf, next: Cols.all.RowOf) =>
    // val test: RNel[Long,RNil.type] = last
    val forward = last.diff(next)
    val backward = next.diff(last)
    val lastDiff = last.diff(last)
    forward(last) == next && backward(next) == last && lastDiff(last) == last
  }

}