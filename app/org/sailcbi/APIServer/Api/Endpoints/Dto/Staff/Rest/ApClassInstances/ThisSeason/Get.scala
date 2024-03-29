package org.sailcbi.APIServer.Api.Endpoints.Dto.Staff.Rest.ApClassInstances.ThisSeason

import play.api.libs.json.{JsValue, Json}

/**
 * !!!!!!!!!!!!
 * This file is AUTO-GENERATED by cbidb-schema
 * Do not manually alter this file, or your changes will be lost
 * !!!!!!!!!!!!
 */
case class DtoStaffRestApClassInstancesThisSeasonGetResponseSuccess (
	instanceId: Int,
	cancelledDatetime: Option[String],
	signupsStartOverride: Option[String],
	signupMin: Option[Int],
	price: Option[Double],
	signupMax: Option[Int],
	formatId: Int,
	hideOnline: Boolean,
	cancelByOverride: Option[String],
	locationString: Option[String],
	doNotAutoCancel: Boolean,
	instructorId: Option[Int],
	$$apClassSessions: List[DtoStaffRestApClassInstancesThisSeasonGetResponseSuccess_ApClassSessions],
	$$instructor: Option[DtoStaffRestApClassInstancesThisSeasonGetResponseSuccess_Instructor],
)

case class DtoStaffRestApClassInstancesThisSeasonGetResponseSuccess_ApClassSessions (
	sessionId: Int,
	instanceId: Int,
	headcount: Option[Int],
	cancelledDatetime: Option[String],
	sessionDatetime: String,
	sessionLength: Double,
	isMakeup: Boolean,
)

object DtoStaffRestApClassInstancesThisSeasonGetResponseSuccess_ApClassSessions {
	implicit val format = Json.format[DtoStaffRestApClassInstancesThisSeasonGetResponseSuccess_ApClassSessions]
	def apply(v: JsValue): DtoStaffRestApClassInstancesThisSeasonGetResponseSuccess_ApClassSessions
		= v.as[DtoStaffRestApClassInstancesThisSeasonGetResponseSuccess_ApClassSessions]
}

case class DtoStaffRestApClassInstancesThisSeasonGetResponseSuccess_Instructor (
	personId: Int,
	nameFirst: Option[String],
	nameLast: Option[String],
)

object DtoStaffRestApClassInstancesThisSeasonGetResponseSuccess_Instructor {
	implicit val format = Json.format[DtoStaffRestApClassInstancesThisSeasonGetResponseSuccess_Instructor]
	def apply(v: JsValue): DtoStaffRestApClassInstancesThisSeasonGetResponseSuccess_Instructor
		= v.as[DtoStaffRestApClassInstancesThisSeasonGetResponseSuccess_Instructor]
}

object DtoStaffRestApClassInstancesThisSeasonGetResponseSuccess {
	implicit val DtoStaffRestApClassInstancesThisSeasonGetResponseSuccess_ApClassSessionsFormat
		= DtoStaffRestApClassInstancesThisSeasonGetResponseSuccess_ApClassSessions.format
	implicit val DtoStaffRestApClassInstancesThisSeasonGetResponseSuccess_InstructorFormat
		= DtoStaffRestApClassInstancesThisSeasonGetResponseSuccess_Instructor.format
	implicit val format = Json.format[DtoStaffRestApClassInstancesThisSeasonGetResponseSuccess]
	def apply(v: JsValue): DtoStaffRestApClassInstancesThisSeasonGetResponseSuccess
		= v.as[DtoStaffRestApClassInstancesThisSeasonGetResponseSuccess]
}

