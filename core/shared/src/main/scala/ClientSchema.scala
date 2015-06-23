package shard

import boopickle._

class ClientSchema[C <: Columns](scalarCols: C = CNil)
  (implicit val rowPickler: Pickler[Row[C]], val rowUnpickler: Unpickler[Row[C]],
            val diffPickler: Pickler[Diff[C]], val diffUnpickler: Unpickler[Diff[C]]) {
  // val scalars = new Table(0, scalarCols)
}