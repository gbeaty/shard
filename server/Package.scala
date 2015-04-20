package shard

import shard.client._

import datomisca._

package object server extends shard.State {
  type One = Cardinality.one.type
  type Many = Cardinality.many.type
  type ConcreteEntity = Entity

  type AttrIdent = datomisca.Keyword
  type Version = datomisca.Database

  case class Entity(underlying: datomisca.Entity) extends super.Entity {
    val id = underlying.id
    lazy val keys = underlying.keySet.map(clojure.lang.Keyword.intern(_))

    def getAny(attrIdent: datomisca.Keyword) = underlying.get(attrIdent)
  }

  /*implicit def datomToChange(datom: Datom)(implicit db: Database) =
    if(datom.added)
      new Assert(datom.id, )*/

  /*type EntityChange = sync.EntityChange[datomisca.Entity]
  type Inserted = sync.Inserted[datomisca.Entity]
  type Updated = sync.Updated[datomisca.Entity]
  type Removed = sync.Removed[datomisca.Entity]*/
}