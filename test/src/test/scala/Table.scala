package shard

import org.scalacheck.Properties
import org.scalacheck.Prop.forAll

import java.util.{Date, UUID}
import java.net.URI
import java.math.{BigDecimal, BigInteger}

/*object RowSpec extends Properties("Changeset") {
  import RowGen._

  property("rowDiffs") = forAll { (last: Row[Cols.all.type], next: Row[Cols.all.type]) =>

    val forward = last.diff(next)
    val backward = next.diff(last)
    val lastDiff = last.diff(last)
    forward(last) == next && backward(next) == last && lastDiff(last) == last
  }

  property("rowIndexes") = forAll { (row: Row[Cols.all.type]) =>
    row.toList.size.equals(row.size)
    row.size.equals(row.index + 1)
  }
}*/