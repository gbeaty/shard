package sync

import sync.client._

import datomisca._

package object server {
  type One = Cardinality.one.type
  type Many = Cardinality.many.type

  type EntityChange = sync.EntityChange[FinalId,datomisca.Entity]
  type Inserted = sync.Inserted[FinalId,datomisca.Entity]
  type Updated = sync.Updated[FinalId,datomisca.Entity]
  type Removed = sync.Removed[FinalId,datomisca.Entity]

  implicit def toEntityId(fid: FinalId) = new EntityId(fid.underlying)
}