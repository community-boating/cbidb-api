package org.sailcbi.APIServer.Api.Endpoints.Dto.Staff.Dockhouse.CreateSignout

import play.api.libs.json.{JsValue, Json}

/**
 * !!!!!!!!!!!!
 * This file is AUTO-GENERATED by cbidb-schema
 * Do not manually alter this file, or your changes will be lost
 * !!!!!!!!!!!!
 */
case class DtoStaffDockhouseCreateSignoutPostResponseSuccess (
	signoutId: Int,
	personId: Option[Int],
	programId: Int,
	boatId: Int,
	signoutType: String,
	cardNum: Option[String],
	sailNumber: Option[String],
	hullNumber: Option[String],
	testRatingId: Option[Int],
	testResult: Option[String],
	isQueued: Boolean,
	signoutDatetime: Option[String],
	$$crew: List[DtoStaffDockhouseCreateSignoutPostResponseSuccess_Crew],
	$$tests: List[DtoStaffDockhouseCreateSignoutPostResponseSuccess_Tests],
)

case class DtoStaffDockhouseCreateSignoutPostResponseSuccess_Crew (
	signoutId: Int,
	personId: Option[Int],
	cardNum: Option[String],
	startActive: Option[String],
)

object DtoStaffDockhouseCreateSignoutPostResponseSuccess_Crew {
	implicit val format = Json.format[DtoStaffDockhouseCreateSignoutPostResponseSuccess_Crew]
	def apply(v: JsValue): DtoStaffDockhouseCreateSignoutPostResponseSuccess_Crew
		= v.as[DtoStaffDockhouseCreateSignoutPostResponseSuccess_Crew]
}

case class DtoStaffDockhouseCreateSignoutPostResponseSuccess_Tests (
	signoutId: Int,
	personId: Int,
	ratingId: Int,
)

object DtoStaffDockhouseCreateSignoutPostResponseSuccess_Tests {
	implicit val format = Json.format[DtoStaffDockhouseCreateSignoutPostResponseSuccess_Tests]
	def apply(v: JsValue): DtoStaffDockhouseCreateSignoutPostResponseSuccess_Tests
		= v.as[DtoStaffDockhouseCreateSignoutPostResponseSuccess_Tests]
}

object DtoStaffDockhouseCreateSignoutPostResponseSuccess {
	implicit val DtoStaffDockhouseCreateSignoutPostResponseSuccess_CrewFormat
		= DtoStaffDockhouseCreateSignoutPostResponseSuccess_Crew.format
	implicit val DtoStaffDockhouseCreateSignoutPostResponseSuccess_TestsFormat
		= DtoStaffDockhouseCreateSignoutPostResponseSuccess_Tests.format
	implicit val format = Json.format[DtoStaffDockhouseCreateSignoutPostResponseSuccess]
	def apply(v: JsValue): DtoStaffDockhouseCreateSignoutPostResponseSuccess
		= v.as[DtoStaffDockhouseCreateSignoutPostResponseSuccess]
}


case class DtoStaffDockhouseCreateSignoutPostRequest (
	skipperPersonId: Int,
	programId: Int,
	skipperCardNumber: String,
	skipperTestRatingId: Option[Int],
	boatId: Int,
	sailNumber: Option[String],
	hullNumber: Option[String],
	classSessionId: Option[Int],
	isRacing: Boolean,
	dockmasterOverride: Boolean,
	didInformKayakRules: Boolean,
	signoutCrew: List[DtoStaffDockhouseCreateSignoutPostRequest_SignoutCrew],
)

case class DtoStaffDockhouseCreateSignoutPostRequest_SignoutCrew (
	personId: Int,
	cardNumber: String,
	testRatingId: Option[Int],
)

object DtoStaffDockhouseCreateSignoutPostRequest_SignoutCrew {
	implicit val format = Json.format[DtoStaffDockhouseCreateSignoutPostRequest_SignoutCrew]
	def apply(v: JsValue): DtoStaffDockhouseCreateSignoutPostRequest_SignoutCrew
		= v.as[DtoStaffDockhouseCreateSignoutPostRequest_SignoutCrew]
}

object DtoStaffDockhouseCreateSignoutPostRequest {
	implicit val DtoStaffDockhouseCreateSignoutPostRequest_SignoutCrewFormat
		= DtoStaffDockhouseCreateSignoutPostRequest_SignoutCrew.format
	implicit val format = Json.format[DtoStaffDockhouseCreateSignoutPostRequest]
	def apply(v: JsValue): DtoStaffDockhouseCreateSignoutPostRequest
		= v.as[DtoStaffDockhouseCreateSignoutPostRequest]
}

