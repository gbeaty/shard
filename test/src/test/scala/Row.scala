package shard.test

import shard._

import org.scalacheck.Properties
import org.scalacheck.Prop.forAll

import java.util.{Date, UUID}
import java.net.URI
import java.math.{BigDecimal, BigInteger}

object ServerRowSpec extends Properties("Server rows") {
  import ServerRowGen._
  import ServerRowGen.platform._
  import shard.test.ServerTestCols._

  property("diffs") = forAll { (last: Row[all.type], next: Row[all.type]) =>

    val forward = last.diff(next)
    val backward = next.diff(last)
    val nowhere = last.diff(last)
    forward(last) == next && backward(next) == last && nowhere(last) == last
  }
}