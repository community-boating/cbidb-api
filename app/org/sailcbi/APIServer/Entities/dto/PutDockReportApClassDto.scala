package org.sailcbi.APIServer.Entities.dto

import com.coleji.neptune.API.ValidationResult
import com.coleji.neptune.Core.UnlockedRequestCache
import com.coleji.neptune.Storable.DTOClass
import org.sailcbi.APIServer.Entities.EntityDefinitions.DockReportApClass
import play.api.libs.json.{JsValue, Json}

import java.time.LocalDateTime

case class PutDockReportApClassDto (
	DOCK_REPORT_AP_CLASS_ID: Option[Int],
	var DOCK_REPORT_ID: Option[Int],
	AP_INSTANCE_ID: Option[Int],
	CLASS_NAME: String,
	CLASS_DATETIME: LocalDateTime,
	LOCATION: Option[String],
	INSTRUCTOR: Option[String],
	ATTEND: Option[Int],
) extends DTOClass[DockReportApClass] {
	override def getId: Option[Int] = DOCK_REPORT_AP_CLASS_ID

	override def mutateStorableForUpdate(s: DockReportApClass): DockReportApClass = {
		s.update(_.dockReportId, DOCK_REPORT_ID.get)
		s.update(_.apInstanceId, AP_INSTANCE_ID)
		s.update(_.className, CLASS_NAME)
		s.update(_.classDatetime, CLASS_DATETIME)
		s.update(_.location, LOCATION)
		s.update(_.instructor, INSTRUCTOR)
		s.update(_.attend, ATTEND)
		s
	}

	override def mutateStorableForInsert(s: DockReportApClass):  DockReportApClass = mutateStorableForUpdate(s)

//	override protected def runValidationsForUpdate(rc: UnlockedRequestCache): ValidationResult = ValidationResult.from("blah")
}

object PutDockReportApClassDto {
	implicit val format = Json.format[PutDockReportApClassDto]

	def apply(v: JsValue): PutDockReportApClassDto = v.as[PutDockReportApClassDto]

	def apply(c: DockReportApClass): PutDockReportApClassDto = new PutDockReportApClassDto(
		DOCK_REPORT_AP_CLASS_ID=Some(c.values.dockReportApClassId.get),
		DOCK_REPORT_ID=Some(c.values.dockReportId.get),
		AP_INSTANCE_ID=c.values.apInstanceId.get,
		CLASS_NAME=c.values.className.get,
		CLASS_DATETIME=c.values.classDatetime.get,
		LOCATION=c.values.location.get,
		INSTRUCTOR=c.values.instructor.get,
		ATTEND=c.values.attend.get,
	)
}