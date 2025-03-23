package org.sailcbi.APIServer.Entities.dto.Square

import play.api.libs.json.{JsValue, Json}

case class PostUpsertOrderShape (orderAppAlias: String)

object PostUpsertOrderShape {
    implicit val format = Json.format[PostUpsertOrderShape]
    def apply(v: JsValue): PostUpsertOrderShape = v.as[PostUpsertOrderShape]
}