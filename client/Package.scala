package sync

package object client {
  type EntityChange = sync.EntityChange[EntityId]
  type Inserted = sync.Inserted[EntityId]
  type Updated = sync.Updated[EntityId]
  type Removed = sync.Removed[EntityId]
}