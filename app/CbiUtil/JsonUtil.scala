package CbiUtil

import play.api.libs.json.{JsArray, JsObject, JsString, JsValue}

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

  // TODO: convert all apis to report runs, get rid of this
  def getMetaData(fields: Seq[String]): JsArray =
    JsArray(
      fields.map(f =>
        JsObject(Map("name" -> JsString(f)))
      )
    )
}
