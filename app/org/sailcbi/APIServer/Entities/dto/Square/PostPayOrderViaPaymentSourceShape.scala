package org.sailcbi.APIServer.Entities.dto.Square

import play.api.libs.json.{JsValue, Json}

case class PostPayOrderViaPaymentSourceShape (paymentSourceId: String, partialPayment: Boolean, orderAppAlias: String, verificationToken: Option[String])

object PostPayOrderViaPaymentSourceShape {
    implicit val format = Json.format[PostPayOrderViaPaymentSourceShape]
    def apply(v: JsValue): PostPayOrderViaPaymentSourceShape = v.as[PostPayOrderViaPaymentSourceShape]
}