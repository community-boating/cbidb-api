package org.sailcbi.APIServer.Entities.dto

import com.coleji.neptune.Storable.DTOClass
import org.sailcbi.APIServer.Entities.EntityDefinitions.DockReportHullCount
import play.api.libs.json.{JsValue, Json}

case class PutDockReportHullCountDto (
	DOCK_REPORT_HULL_CT_ID: Option[Int],
	var DOCK_REPORT_ID: Option[Int],
	HULL_TYPE: String,
	IN_SERVICE: Option[Int],
	STAFF_TALLY: Option[Int],
) extends DTOClass[DockReportHullCount] {
	override def getId: Option[Int] = DOCK_REPORT_HULL_CT_ID

	override def mutateStorableForUpdate(s: DockReportHullCount): DockReportHullCount = {
		s.update(_.dockReportId, DOCK_REPORT_ID.get)
		s.update(_.hullType, HULL_TYPE)
		s.update(_.inService, IN_SERVICE)
		s.update(_.staffTally, STAFF_TALLY)
		s
	}

	override def mutateStorableForInsert(s: DockReportHullCount):  DockReportHullCount = mutateStorableForUpdate(s)
}

object PutDockReportHullCountDto {
	implicit val format = Json.format[PutDockReportHullCountDto]

	def apply(v: JsValue): PutDockReportHullCountDto = v.as[PutDockReportHullCountDto]

	def apply (c: DockReportHullCount): PutDockReportHullCountDto = new PutDockReportHullCountDto(
		DOCK_REPORT_HULL_CT_ID = Some(c.values.dockReportHullCtId.get),
		DOCK_REPORT_ID = Some(c.values.dockReportId.get),
		HULL_TYPE = c.values.hullType.get,
		IN_SERVICE = c.values.inService.get,
		STAFF_TALLY = c.values.staffTally.get,
	)
}