package org.sailcbi.APIServer.Entities.dto

import com.coleji.neptune.Storable.DTOClass
import org.sailcbi.APIServer.Entities.EntityDefinitions.DockReportWeather
import play.api.libs.json.{JsValue, Json}

import java.time.LocalDateTime

case class PutDockReportWeatherDto (
	WEATHER_ID: Option[Int],
	var DOCK_REPORT_ID: Option[Int],
	WEATHER_DATETIME: LocalDateTime,
	TEMP: Option[Double],
	WEATHER_SUMMARY: Option[String],
	WIND_DIR: Option[String],
	WIND_SPEED_KTS_STEADY: Option[Double],
	WIND_SPEED_KTS_GUST: Option[Double],
	RESTRICTIONS: Option[String],
) extends DTOClass[DockReportWeather] {
	override def getId: Option[Int] = WEATHER_ID

	override def mutateStorableForUpdate(s: DockReportWeather): DockReportWeather = {
		s.update(_.dockReportId, DOCK_REPORT_ID.get)
		s.update(_.weatherDatetime, WEATHER_DATETIME)
		s.update(_.temp, TEMP)
		s.update(_.weatherSummary, WEATHER_SUMMARY)
		s.update(_.windDir, WIND_DIR)
		s.update(_.windSpeedKtsSteady, WIND_SPEED_KTS_STEADY)
		s.update(_.windSpeedKtsGust, WIND_SPEED_KTS_GUST)
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
		DOCK_REPORT_ID=Some(w.values.dockReportId.get),
		WEATHER_DATETIME=w.values.weatherDatetime.get,
		TEMP=w.values.temp.get,
		WEATHER_SUMMARY=w.values.weatherSummary.get,
		WIND_DIR=w.values.windDir.get,
		WIND_SPEED_KTS_STEADY=w.values.windSpeedKtsSteady.get,
		WIND_SPEED_KTS_GUST=w.values.windSpeedKtsGust.get,
		RESTRICTIONS=w.values.restrictions.get,
	)
}