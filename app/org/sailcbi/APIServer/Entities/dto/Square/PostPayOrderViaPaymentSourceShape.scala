package org.sailcbi.APIServer.Entities.dto.Square

import play.api.libs.json.{JsValue, Json}

case class PostPayOrderViaPaymentSourceShape (paymentSourceId: String, orderAppAlias: String)

object PostPayOrderViaPaymentSourceShape {
    implicit val format = Json.format[PostPayOrderViaPaymentSourceShape]
    def apply(v: JsValue): PostPayOrderViaPaymentSourceShape = v.as[PostPayOrderViaPaymentSourceShape]
}