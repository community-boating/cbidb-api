package org.sailcbi.APIServer.Entities.dto.Square

import play.api.libs.json.{JsValue, Json}

case class GenericPostWithOrderAppAliasShape(orderAppAlias: String)

object GenericPostWithOrderAppAliasShape {
    implicit val format = Json.format[GenericPostWithOrderAppAliasShape]
    def apply(v: JsValue): GenericPostWithOrderAppAliasShape = v.as[GenericPostWithOrderAppAliasShape]
}