package shard

import scalajs._

package object js {
  type Row[C <: CList] = Platform.Row[C]
  type Diff[C <: CList] = Platform.Diff[C]
}