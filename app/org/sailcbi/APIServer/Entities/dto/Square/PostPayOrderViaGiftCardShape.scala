package org.sailcbi.APIServer.Entities.dto.Square

import play.api.libs.json.{JsValue, Json}

case class PostPayOrderViaGiftCardShape (orderAppAlias: String)

object PostPayOrderViaGiftCardShape {
    implicit val format = Json.format[PostPayOrderViaGiftCardShape]
    def apply(v: JsValue): PostPayOrderViaGiftCardShape = v.as[PostPayOrderViaGiftCardShape]
}