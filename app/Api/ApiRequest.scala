package Api

import java.time.{LocalDateTime, ZoneId, ZoneOffset, ZonedDateTime}
import java.time.format.DateTimeFormatter
import javax.inject.Inject

import CbiUtil.JsonUtil
import Services.CacheBroker
import play.api.libs.json.{JsObject, JsString, JsValue, Json}


abstract class ApiRequest(cb: CacheBroker) {
  val cacheExpirationDatePattern: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")
  protected val cacheExpiresKeyName = "$cacheExpires"
  protected def formatTime(time: LocalDateTime): String = {
    val zonedTime: ZonedDateTime = time.atZone(ZoneId.systemDefault())
    zonedTime.withZoneSameInstant(ZoneOffset.UTC).format(cacheExpirationDatePattern)
  }

  def getCacheBrokerKey: String
  def getExpirationTime: LocalDateTime

  def tryCache: Option[String] = {
    cb.get(getCacheBrokerKey) match {
      case Some(s) => {
        println("cache hit")
        val value: JsValue = Json.parse(s)
        val data: JsObject = JsonUtil.getProperty[JsObject](value, "data")
        val cacheExpiration: String = JsonUtil.getProperty[JsString](data, cacheExpiresKeyName).value
        val expires: ZonedDateTime =
          ZonedDateTime
          .parse(cacheExpiration, cacheExpirationDatePattern)
          .withZoneSameInstant(ZoneId.systemDefault)
        if (expires.isBefore(ZonedDateTime.now)) None
        else Some(s)
      }
      case None => None
    }
  }

  def saveToCache(result: String): Unit = cb.set(getCacheBrokerKey, result)
}
