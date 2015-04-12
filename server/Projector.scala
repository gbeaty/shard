package sync.server

import sync._

import datomisca._

trait Projector {
  def project(attrId: Int)(implicit db: Database): Boolean

  def apply(change: EntityChange)(implicit db: Database) = change match {
    case Inserted(entity) =>
    case Updated(_, _, changes) => changes.keySet.filter(project(_))
    case Removed(entity) => client.Removed(new client.EntityId(entity.id))
  }
}

object ProjectAll extends Projector {
  def project(attrId: Int)(implicit db: Database) = true
}

case class ProjectorAttrs(attrs: Set[Attribute[_,_ <: Cardinality]]) extends Projector {
  val idents = attrs.map(_.ident.toString)
  def project(attrId: Int)(implicit db: Database) =
    db.entity(attrId).getAs[String](Attribute.ident).exists(idents.contains(_))
}