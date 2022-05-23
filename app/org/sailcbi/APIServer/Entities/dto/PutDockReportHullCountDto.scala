package org.sailcbi.APIServer.Entities.dto

import com.coleji.neptune.Storable.DTOClass
import org.sailcbi.APIServer.Entities.EntityDefinitions.DockReportHullCount
import play.api.libs.json.{JsValue, Json}

case class PutDockReportHullCountDto (
	dockReportHullCtId: Option[Int],
	dockReportId: Int,
	hullType: Option[String],
	inService: Option[Double],
	staffTally: Option[Double],
) extends DTOClass[DockReportHullCount] {
	override def getId: Option[Int] = dockReportHullCtId

	override def mutateStorableForUpdate(s: DockReportHullCount): DockReportHullCount = {
		s.update(_.dockReportId, dockReportId)
		s.update(_.hullType, hullType)
		s.update(_.inService, inService)
		s.update(_.staffTally, staffTally)
		s
	}

	override def mutateStorableForInsert(s: DockReportHullCount):  DockReportHullCount = mutateStorableForUpdate(s)
}

object PutDockReportHullCountDto {
	implicit val format = Json.format[PutDockReportHullCountDto]

	def apply(v: JsValue): PutDockReportHullCountDto = v.as[PutDockReportHullCountDto]
}