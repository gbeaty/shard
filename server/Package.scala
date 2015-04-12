package sync

import sync.client._

import datomisca._

package object server {
  type One = Cardinality.one.type
  type Many = Cardinality.many.type

  type EntityChange = sync.EntityChange[Entity]
  type Inserted = sync.Inserted[Entity]
  type Updated = sync.Updated[Entity]
  type Removed = sync.Removed[Entity]
}