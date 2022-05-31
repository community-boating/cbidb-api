package org.sailcbi.APIServer.Entities.dto

import com.coleji.neptune.Storable.DTOClass
import org.sailcbi.APIServer.Entities.EntityDefinitions.DockReportStaff
import play.api.libs.json.{JsValue, Json}

import java.time.LocalDateTime

case class PutDockReportStaffDto (
	DOCK_REPORT_STAFF_ID: Option[Int],
	var DOCK_REPORT_ID: Option[Int],
	DOCKMASTER_ON_DUTY: Boolean,
	STAFF_NAME: String,
	TIME_IN: Option[LocalDateTime],
	TIME_OUT: Option[LocalDateTime],
) extends DTOClass[DockReportStaff] {
	override def getId: Option[Int] = DOCK_REPORT_STAFF_ID

	override def mutateStorableForUpdate(s: DockReportStaff): DockReportStaff = {
		s.update(_.dockReportId, DOCK_REPORT_ID.get)
		s.update(_.dockmasterOnDuty, DOCKMASTER_ON_DUTY)
		s.update(_.staffName, STAFF_NAME)
		s.update(_.timeIn, TIME_IN)
		s.update(_.timeOut, TIME_OUT)
		s
	}

	override def mutateStorableForInsert(s: DockReportStaff):  DockReportStaff = mutateStorableForUpdate(s)
}

object PutDockReportStaffDto {
	implicit val format = Json.format[PutDockReportStaffDto]

	def apply(v: JsValue): PutDockReportStaffDto = v.as[PutDockReportStaffDto]

	def apply(s: DockReportStaff): PutDockReportStaffDto = new PutDockReportStaffDto(
		DOCK_REPORT_STAFF_ID=Some(s.values.dockReportStaffId.get),
		DOCK_REPORT_ID=Some(s.values.dockReportId.get),
		DOCKMASTER_ON_DUTY=s.values.dockmasterOnDuty.get,
		STAFF_NAME=s.values.staffName.get,
		TIME_IN=s.values.timeIn.get,
		TIME_OUT=s.values.timeOut.get,
	)
}