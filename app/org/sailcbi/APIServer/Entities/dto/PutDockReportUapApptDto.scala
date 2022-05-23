package org.sailcbi.APIServer.Entities.dto

import com.coleji.neptune.Storable.DTOClass
import org.sailcbi.APIServer.Entities.EntityDefinitions.DockReportUapAppt
import play.api.libs.json.{JsValue, Json}

import java.time.LocalDateTime

case class PutDockReportUapApptDto (
	dockReportApptId: Option[Int],
	dockReportId: Int,
	apptDatetime: Option[LocalDateTime],
	apptType: Option[String],
	participantName: Option[String],
	boatTypeId: Option[Int],
	instructorName: Option[String],
) extends DTOClass[DockReportUapAppt] {
	override def getId: Option[Int] = dockReportApptId

	override def mutateStorableForUpdate(s: DockReportUapAppt): DockReportUapAppt = {
		s.update(_.dockReportId, dockReportId)
		s.update(_.apptDatetime, apptDatetime)
		s.update(_.apptType, apptType)
		s.update(_.participantName, participantName)
		s.update(_.boatTypeId, boatTypeId)
		s.update(_.instructorName, instructorName)
		s
	}

	override def mutateStorableForInsert(s: DockReportUapAppt):  DockReportUapAppt = mutateStorableForUpdate(s)
}

object PutDockReportUapApptDto {
	implicit val format = Json.format[PutDockReportUapApptDto]

	def apply(v: JsValue): PutDockReportUapApptDto = v.as[PutDockReportUapApptDto]
}