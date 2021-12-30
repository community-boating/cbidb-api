package org.sailcbi.APIServer.Entities.dto

import play.api.libs.json.{JsValue, Json}

import java.time.{LocalDate, LocalDateTime}

case class MembershipSale (
	assignId: Int,
	membershipTypeId: Int,
	programId: Int,
	discountInstanceId: Option[Int],
	closeId: Option[Int],
	voidCloseId: Option[Int],
	purchaseDate: Option[LocalDateTime],
	startDate: Option[LocalDate],
	expirationDate: Option[LocalDate],
	price: Double,
	saleClosedDatetime: Option[LocalDateTime],
	voidClosedDatetime: Option[LocalDateTime],
)

object MembershipSale {
	implicit val format = Json.format[MembershipSale]
	def apply(v: JsValue): MembershipSale = v.as[MembershipSale]
}

