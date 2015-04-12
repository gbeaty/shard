package sync

import sync.client._

import datomisca._

package object server {
  type One = Cardinality.one.type
  type Many = Cardinality.many.type

  type EntityChange = sync.EntityChange[FinalId]
  type Inserted = sync.Inserted[FinalId]
  type Updated = sync.Updated[FinalId]
  type Removed = sync.Removed[FinalId]
}