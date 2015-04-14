package sync

package object client {
  type EntityChange = sync.EntityChange[Long]
  type Inserted = sync.Inserted[Long]
  type Updated = sync.Updated[Long]
  type Removed = sync.Removed[Long]
}