package shard

import datomisca._
import boopickle._
import java.util.Date
import java.net.URI
import java.util.UUID
import scala.reflect._

package object server {
  implicit val platform = new shard.server.Platform
}