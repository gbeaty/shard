package sync.server

import sync._
import sync.client._

import datomisca._

case class Changeset(beforeDb: Database, afterDb: Database, changes: Map[Entity,EntityChange]) {
  def merge(next: Changeset) = Changeset(
    beforeDb,
    next.afterDb,
    next.changes.foldLeft(changes) { (res,kv) =>
      val (entity, nextEC) = kv
      val replacement = (changes.get(entity), nextEC) match {
        case (None, ec) => Some(ec)
        case (Some(ins: Inserted), rem: Removed) => None
        case (Some(ins: Inserted), up: Updated) => Some(ins)
        case (Some(up: Updated), upn: Updated) => Some(upn) // FIX ME
        case (Some(up: Updated), rem: Removed) => Some(rem)
        case (Some(rem: Removed), ins: Inserted) => Some(ins) // FIX ME
      }
      replacement.map(r => res + (entity -> r)).getOrElse(res)
    }
  )
}
object Changeset {
  def apply(reports: Set[TxReport]): Changeset = {
    val dbBefore = reports.map(_.dbBefore).minBy(_.basisT)
    val dbAfter = reports.map(_.dbAfter).maxBy(_.basisT)
    Changeset(
      dbBefore,
      dbAfter,
      Map[Entity,EntityChange]()
      /*reports.map(_.txData).flatten.map(EntityChange(_))
      reports.map(_.txData).flatten.foldLeft(Map[Entity,EntityChange]()) { (res,datom) =>
        val id = datom.id
        lazy val entityBefore = dbBefore.entity(id)
        lazy val entityAfter = dbAfter.entity(id)

        if(datom.added && !Util.entityExists(id)(dbBefore))
          Inserted(dbAfter.entity(id))
        else if(!datom.added && Util.entityExists(id)(dbAfter))
          Updated()
      }*/
    )
  }
}

object EntityChange {
  def entityExists(entity: Entity) = entity.keySet.size > 0

  def apply(txReport: TxReport) = {
    val dbBefore = txReport.dbBefore
    val dbAfter = txReport.dbAfter

    txReport.txData.groupBy(_.id).map { kv =>
      val (id, datoms) = kv
      val entityBefore = dbBefore.entity(id)
      val entityAfter = dbAfter.entity(id)      
      
      val entityChange = (entityExists(entityBefore), entityExists(entityAfter)) match {
        case (false, true) => Inserted(entityAfter)
        case (true, true) =>
          Updated(entityBefore, entityAfter, datoms.groupBy(_.attrId).map { kv =>
            val (attrId, datoms) = kv
            (attrId -> FactChange(datoms.map(datom => (datom.value -> datom.added)).toMap))
          }.toMap)
        case (true, false) => Removed(entityBefore)
      }

      kv
    }
  }
}
trait EntityChange {
  val entity: Entity
}
case class Inserted(entity: Entity) extends EntityChange
case class Updated(before: Entity, after: Entity, changes: Map[Int,FactChange[_]]) extends EntityChange {
  val entity = after
  def merge(next: Updated) = Updated(
    before,
    next.after,
    next.changes.foldLeft(changes) { (res,kv) =>
      val attrId = kv._1
      val nextChange = kv._2.asInstanceOf[FactChange[Any]]
      changes.get(attrId).map( oc =>
        oc.asInstanceOf[FactChange[Any]].merge(nextChange).map(m => res + (attrId -> m)).getOrElse(res - attrId)
        // oc => res + (attrId -> oc.asInstanceOf[FactChange[Any]].merge(nextChange))
      ).getOrElse(res + kv)
    }
  )
}
case class Removed(entity: Entity) extends EntityChange

trait AttrUpdate
case class OneAttrUpdate[V](value: V)
case class ManyAttrUpdate[V](values: Map[V,Boolean])
case class EntityUpdate(ones: Map[Int,FactChange[_]], manies: Map[Int,Map[Any,Boolean]])