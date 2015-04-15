package sync.client

import scalajs._

class Changeset(beforeDb: Long, afterDb: Long, changes: js.Dictionary[EntityChange])