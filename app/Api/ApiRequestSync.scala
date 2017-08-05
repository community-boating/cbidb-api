package Api

import CbiUtil.JsonUtil
import Services.CacheBroker
import play.api.libs.json.{JsObject, JsString, JsValue, Json}

abstract class ApiRequestSync(cb: CacheBroker) extends ApiRequest(cb) {
  def getJSONResult: JsObject

  def get: String = {
    val cbKey = getCacheBrokerKey
    tryCache match {
      case Some(s) => s
      case None => {
        println("cache miss")
        val json: JsObject = getJSONResult
        val newData: JsObject = json + (cacheExpiresKeyName, JsString(formatTime(getExpirationTime)))
        val result: String = new JsObject(Map("data" -> newData)).toString()

        saveToCache(result)
        result
      }
    }
  }
}
