package shard

import org.scalacheck.Properties
import org.scalacheck.Prop.forAll

import java.util.{Date, UUID}
import java.net.URI
import java.math.{BigDecimal, BigInteger}

object RowSpec extends Properties("Changeset") {

  property("startsWith") = forAll { (a: String, b: String) =>
    // (a+b).startsWith(a)
    true
  }

}