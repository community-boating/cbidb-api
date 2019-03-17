package Api.Endpoints.Member

import play.api.libs.json.{JsValue, Json}

case class RequiredInfoShape(firstName: Option[String], lastName: Option[String], middleInitial: Option[String])

object RequiredInfoShape {
  implicit val format = Json.format[RequiredInfoShape]
  def apply(v: JsValue): RequiredInfoShape = v.as[RequiredInfoShape]
}
