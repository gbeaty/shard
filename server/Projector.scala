package sync.server

import sync._

import datomisca._

case class Projector(id: String, attrs: Set[Attribute[_,_ <: Cardinality]]) {
  val idents = attrs.map(_.ident.toString)
  def project(attrId: Int)(implicit db: Database) =
    db.entity(attrId).getAs[String](Attribute.ident).exists(idents.contains(_))

  def apply(change: EntityChange) = change match {
    case Inserted(id, entity) =>
    case Updated(id, changes) =>
    case Removed(id) => new client.Removed(id)
  }
}