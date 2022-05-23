package org.sailcbi.APIServer.Entities.dto

import com.coleji.neptune.Storable.DTOClass
import org.sailcbi.APIServer.Entities.EntityDefinitions.DockReportStaff
import play.api.libs.json.{JsValue, Json}

import java.time.LocalDateTime

case class PutDockReportStaffDto (
	dockReportStaffId: Option[Int],
	dockReportId: Int,
	dockmasterOnDuty: Option[Boolean],
	staffName: Option[String],
	timeIn: Option[LocalDateTime],
	timeOut: Option[LocalDateTime],
) extends DTOClass[DockReportStaff] {
	override def getId: Option[Int] = dockReportStaffId

	override def mutateStorableForUpdate(s: DockReportStaff): DockReportStaff = {
		s.update(_.dockReportId, dockReportId)
		s.update(_.dockmasterOnDuty, dockmasterOnDuty)
		s.update(_.staffName, staffName)
		s.update(_.timeIn, timeIn)
		s.update(_.timeOut, timeOut)
		s
	}

	override def mutateStorableForInsert(s: DockReportStaff):  DockReportStaff = mutateStorableForUpdate(s)
}

object PutDockReportStaffDto {
	implicit val format = Json.format[PutDockReportStaffDto]

	def apply(v: JsValue): PutDockReportStaffDto = v.as[PutDockReportStaffDto]
}