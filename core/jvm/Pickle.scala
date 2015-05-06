package shard.server

import shard._
import boopickle._
import datomisca._
import java.util.Date
import java.net.URI
import java.util.UUID

object Pickle {
  implicit val attrDiffPickler = CompositePickler[AttrDiff]
    .addConcreteType[OneAttrDiff[Long]]
    .addConcreteType[ManyAttrDiff[Long]]
    .addConcreteType[OneAttrDiff[String]]
    .addConcreteType[ManyAttrDiff[String]]
    .addConcreteType[OneAttrDiff[Float]]
    .addConcreteType[ManyAttrDiff[Float]]
    .addConcreteType[OneAttrDiff[Double]]
    .addConcreteType[ManyAttrDiff[Double]]
    .addConcreteType[OneAttrDiff[Boolean]]
    .addConcreteType[ManyAttrDiff[Boolean]]
    .addConcreteType[OneAttrDiff[UUID]]
    .addConcreteType[ManyAttrDiff[UUID]]
    .addConcreteType[OneAttrDiff[Array[Byte]]]
    .addConcreteType[ManyAttrDiff[Array[Byte]]]

  implicit val entityChangePickler = CompositePickler[EntityChange]
    .addConcreteType[Upserted]
    .addConcreteType[Removed]

  def changeset(cs: ServerChangeset) = boopickle.Pickle(cs.changes)
}
object Unpickle {
  import Pickle._
}