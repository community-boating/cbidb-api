package org.sailcbi.APIServer.Entities.dto

import com.coleji.neptune.Storable.DTOClass
import org.sailcbi.APIServer.Entities.EntityDefinitions.DockReportWeather
import play.api.libs.json.{JsValue, Json}

import java.time.LocalDateTime

case class PutDockReportWeatherDto (
	weatherId: Option[Int],
	dockReportId: Int,
	weatherDatetime: Option[LocalDateTime],
	temp: Option[Double],
	weatherSummary: Option[String],
	windDir: Option[String],
	windSpeedKts: Option[Double],
	restrictions: Option[String],
) extends DTOClass[DockReportWeather] {
	override def getId: Option[Int] = weatherId

	override def mutateStorableForUpdate(s: DockReportWeather): DockReportWeather = {
		s.update(_.dockReportId, dockReportId)
		s.update(_.weatherDatetime, weatherDatetime)
		s.update(_.temp, temp)
		s.update(_.weatherSummary, weatherSummary)
		s.update(_.windDir, windDir)
		s.update(_.windSpeedKts, windSpeedKts)
		s.update(_.restrictions, restrictions)
		s
	}

	override def mutateStorableForInsert(s: DockReportWeather):  DockReportWeather = mutateStorableForUpdate(s)
}

object PutDockReportWeatherDto {
	implicit val format = Json.format[PutDockReportWeatherDto]

	def apply(v: JsValue): PutDockReportWeatherDto = v.as[PutDockReportWeatherDto]
}