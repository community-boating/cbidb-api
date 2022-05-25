package org.sailcbi.APIServer.Entities.dto

import com.coleji.neptune.Storable.DTOClass
import org.sailcbi.APIServer.Entities.EntityDefinitions.DockReportWeather
import play.api.libs.json.{JsValue, Json}

import java.time.LocalDateTime

case class PutDockReportWeatherDto (
	WEATHER_ID: Option[Int],
	var DOCK_REPORT_ID: Int,
	WEATHER_DATETIME: Option[LocalDateTime],
	TEMP: Option[Double],
	WEATHER_SUMMARY: Option[String],
	WIND_DIR: Option[String],
	WIND_SPEED_KTS: Option[Double],
	RESTRICTIONS: Option[String],
) extends DTOClass[DockReportWeather] {
	override def getId: Option[Int] = WEATHER_ID

	override def mutateStorableForUpdate(s: DockReportWeather): DockReportWeather = {
		s.update(_.dockReportId, DOCK_REPORT_ID)
		s.update(_.weatherDatetime, WEATHER_DATETIME)
		s.update(_.temp, TEMP)
		s.update(_.weatherSummary, WEATHER_SUMMARY)
		s.update(_.windDir, WIND_DIR)
		s.update(_.windSpeedKts, WIND_SPEED_KTS)
		s.update(_.restrictions, RESTRICTIONS)
		s
	}

	override def mutateStorableForInsert(s: DockReportWeather):  DockReportWeather = mutateStorableForUpdate(s)
}

object PutDockReportWeatherDto {
	implicit val format = Json.format[PutDockReportWeatherDto]

	def apply(v: JsValue): PutDockReportWeatherDto = v.as[PutDockReportWeatherDto]

	def apply(w: DockReportWeather): PutDockReportWeatherDto = new PutDockReportWeatherDto(
		WEATHER_ID=Some(w.values.weatherId.get),
		DOCK_REPORT_ID=w.values.dockReportId.get,
		WEATHER_DATETIME=w.values.weatherDatetime.get,
		TEMP=w.values.temp.get,
		WEATHER_SUMMARY=w.values.weatherSummary.get,
		WIND_DIR=w.values.windDir.get,
		WIND_SPEED_KTS=w.values.windSpeedKts.get,
		RESTRICTIONS=w.values.restrictions.get,
	)
}