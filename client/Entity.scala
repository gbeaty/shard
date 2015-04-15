package sync.client

import sync._

import scalajs._

class Entity(val id: String, val datas: js.Dictionary[js.Any]) {
  def get[A](attrId: OneAttrId[A]) = datas.get(attrId.id.toString).map(_.asInstanceOf[A])
  def get[A](attrId: ManyAttrId[A]) =
    datas.get(attrId.id.toString).map(_.asInstanceOf[js.Array[A]]).getOrElse(js.Array[A]())
}