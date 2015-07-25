package shard

trait Page {
  val tables: Set[Table]
  val url: String

  val tableIds = Range(1,tables.size).zip(tables).toMap

  case class Refresh()
  case class Update()
}