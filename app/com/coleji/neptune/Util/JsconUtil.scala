package com.coleji.neptune.Util

import play.api.libs.json.{JsArray, JsObject, JsString, JsValue}

import java.time.format.DateTimeFormatter
import java.time.{ZoneOffset, ZonedDateTime}

object JsconUtil {
	val cacheExpirationDatePattern: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")

	def formatJscon(rows: List[List[JsValue]], fieldNames: List[String], cacheCreated: ZonedDateTime, cacheExpires: ZonedDateTime): JsValue = {
		JsObject(Map(
			"data" -> JsObject(Map(
				"rows" -> JsArray(rows.map(row => JsArray(row))),
				"metaData" -> JsArray(fieldNames.map(f => JsObject(Map(
					"name" -> JsString(f)
				)))),
				"$cacheCreated" -> JsString(cacheCreated.withZoneSameInstant(ZoneOffset.UTC).format(cacheExpirationDatePattern)),
				"$cacheExpires" -> JsString(cacheExpires.withZoneSameInstant(ZoneOffset.UTC).format(cacheExpirationDatePattern))
			))
		))
	}
}
