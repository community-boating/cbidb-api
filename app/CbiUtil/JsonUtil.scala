package CbiUtil

import play.api.libs.json.{JsObject, JsValue}

object JsonUtil {
  def getProperty[T](value: JsValue, prop: String): T = {
    value match {
      case o: JsObject => o.fields.find(f => f._1 == prop) match {
        case Some((_, v: T)) => v
        case _ => throw new Exception("Property didnt exist")
      }
      case _ => throw new Exception("Property didnt exist")
    }
  }
}
