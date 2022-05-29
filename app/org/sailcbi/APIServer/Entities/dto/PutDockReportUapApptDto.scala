package org.sailcbi.APIServer.Entities.dto

import com.coleji.neptune.Storable.DTOClass
import org.sailcbi.APIServer.Entities.EntityDefinitions.DockReportUapAppt
import play.api.libs.json.{JsValue, Json}

import java.time.LocalDateTime

case class PutDockReportUapApptDto (
	DOCK_REPORT_APPT_ID: Option[Int],
	var DOCK_REPORT_ID: Option[Int],
	APPT_DATETIME: Option[LocalDateTime],
	APPT_TYPE: Option[String],
	PARTICIPANT_NAME: String,
	BOAT_TYPE_ID: Option[Int],
	INSTRUCTOR_NAME: Option[String],
) extends DTOClass[DockReportUapAppt] {
	override def getId: Option[Int] = DOCK_REPORT_APPT_ID

	override def mutateStorableForUpdate(s: DockReportUapAppt): DockReportUapAppt = {
		s.update(_.dockReportId, DOCK_REPORT_ID.get)
		s.update(_.apptDatetime, APPT_DATETIME)
		s.update(_.apptType, APPT_TYPE)
		s.update(_.participantName, PARTICIPANT_NAME)
		s.update(_.boatTypeId, BOAT_TYPE_ID)
		s.update(_.instructorName, INSTRUCTOR_NAME)
		s
	}

	override def mutateStorableForInsert(s: DockReportUapAppt):  DockReportUapAppt = mutateStorableForUpdate(s)
}

object PutDockReportUapApptDto {
	implicit val format = Json.format[PutDockReportUapApptDto]

	def apply(v: JsValue): PutDockReportUapApptDto = v.as[PutDockReportUapApptDto]

	def apply(u: DockReportUapAppt): PutDockReportUapApptDto = new PutDockReportUapApptDto(
		DOCK_REPORT_APPT_ID=Some(u.values.dockReportApptId.get),
		DOCK_REPORT_ID=Some(u.values.dockReportId.get),
		APPT_DATETIME=u.values.apptDatetime.get,
		APPT_TYPE=u.values.apptType.get,
		PARTICIPANT_NAME=u.values.participantName.get,
		BOAT_TYPE_ID=u.values.boatTypeId.get,
		INSTRUCTOR_NAME=u.values.instructorName.get,
	)
}