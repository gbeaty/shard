package sync.server

import sync._
import sync.client._

import datomisca._

object EntityChange {
  def entityExists(entity: datomisca.Entity) = entity.keySet.size > 0

  def apply(txReport: TxReport): Map[FinalId,EntityChange] = {
    val dbBefore = txReport.dbBefore
    val dbAfter = txReport.dbAfter

    txReport.txData.groupBy(_.id).map { kv =>
      val id = new FinalId(kv._1)
      val datoms = kv._2
      val entityBefore = dbBefore.entity(id)
      val entityAfter = dbAfter.entity(id)

      id -> (if(!entityExists(entityBefore))
        new Inserted(entityAfter)
      else
        if(entityExists(entityAfter))
          new Updated(datoms.groupBy(_.attrId).map { kv =>
            val (attrId, datoms) = kv
            (attrId -> FactChange(datoms.map(datom => (datom.value -> datom.added)).toMap))
          }.toMap)
        else
          new Removed())
    }.toMap
  }
}