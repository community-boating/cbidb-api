package org.sailcbi.APIServer.Entities.dto

import com.coleji.neptune.Storable.DTOClass
import org.sailcbi.APIServer.Entities.EntityDefinitions.DockReportApClass
import play.api.libs.json.{JsValue, Json}

import java.time.LocalDateTime

case class PutDockReportApClassDto (
	dockReportApClassId: Option[Int],
	dockReportId: Int,
	apInstanceId: Option[Int],
	className: Option[String],
	classDatetime: Option[LocalDateTime],
	location: Option[String],
	instructor: Option[String],
	attend: Option[Double],
) extends DTOClass[DockReportApClass] {
	override def getId: Option[Int] = dockReportApClassId

	override def mutateStorableForUpdate(s: DockReportApClass): DockReportApClass = {
		s.update(_.dockReportId, dockReportId)
		s.update(_.apInstanceId, apInstanceId)
		s.update(_.className, className)
		s.update(_.classDatetime, classDatetime)
		s.update(_.location, location)
		s.update(_.instructor, instructor)
		s.update(_.attend, attend)
		s
	}

	override def mutateStorableForInsert(s: DockReportApClass):  DockReportApClass = mutateStorableForUpdate(s)
}

object PutDockReportApClassDto {
	implicit val format = Json.format[PutDockReportApClassDto]

	def apply(v: JsValue): PutDockReportApClassDto = v.as[PutDockReportApClassDto]
}