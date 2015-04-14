package sync

import sync.client._

import datomisca._

package object server {
  type One = Cardinality.one.type
  type Many = Cardinality.many.type

  type EntityChange = sync.EntityChange[datomisca.Entity]
  type Inserted = sync.Inserted[datomisca.Entity]
  type Updated = sync.Updated[datomisca.Entity]
  type Removed = sync.Removed[datomisca.Entity]
}