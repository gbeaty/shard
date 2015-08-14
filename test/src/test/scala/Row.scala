package shard.test

import shard._

import org.scalacheck.Properties
import org.scalacheck.Prop.forAll

import java.util.{Date, UUID}
import java.net.URI
import java.math.{BigDecimal, BigInteger}

abstract class RowSpec[P <: PlatformOf[P]]
  (val schema: TestCols[P], val rowGen: RowGen[P])(implicit val platform: P) extends Properties("Server rows") {

    import org.scalacheck._
    val cols: Cols[P] = schema.all
    implicit val arbGen: Arbitrary[Row[P,schema.all.type]]    

    property("diffs") = forAll { (last: Row[P,schema.all.type], next: Row[P,schema.all.type]) =>

      val forward = last.diff(next)(platform)
      val backward = next.diff(last)(platform)
      val nowhere = last.diff(last)(platform)
      forward(last)(platform) == next && backward(next)(platform) == last && nowhere(last)(platform) == last
    }
}

// object ServerRowSpec extends RowSpec(ServerTestCols, ServerRowGen)


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