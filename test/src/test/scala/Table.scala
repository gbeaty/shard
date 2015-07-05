package shard.test

import shard._

import org.scalacheck.Properties
import org.scalacheck.Prop.forAll

import java.util.{Date, UUID}
import java.net.URI
import java.math.{BigDecimal, BigInteger}

object RowSpec extends Properties("Changeset") {
  import ServerRowGen._
  import ServerRowGen.platform._
  import shard.test.TestCols._

  property("rowDiffs") = forAll { (last: Row[all.type], next: Row[all.type]) =>

    val forward = diff(last, next)
    val backward = diff(next, last)
    val lastDiff = diff(last, last)
    update(last, forward) == next && update(next, backward) == last && update(last, lastDiff) == last
  }
}