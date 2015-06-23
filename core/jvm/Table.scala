package shard.server

import shard._
import datomisca._
import clojure.lang.Keyword

object Col {
  /*def apply[V](kw: Keyword)(implicit db: Database) = try {
    Some(ColOf[V](db.entity(kw).id))
  } catch {
    case e: Throwable => None
  }
  
  def apply[V](kw: String)(implicit db: Database): Option[ColOf[V]] = try {
    apply[V](Keyword.intern(kw))
  } catch {
    case e: Throwable => None
  }*/
}