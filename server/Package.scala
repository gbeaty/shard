package sync

import sync.client._

import datomisca._

package object server {
  type One = Cardinality.one.type
  type Many = Cardinality.many.type
}