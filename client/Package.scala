package sync

package object client {
  type EntityChange = sync.EntityChange[EntityId,Long]
  type Inserted = sync.Inserted[EntityId,Long]
  type Updated = sync.Updated[EntityId,Long]
  type Removed = sync.Removed[EntityId,Long]
}