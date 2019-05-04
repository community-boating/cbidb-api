package Api.Endpoints.Member

import play.api.libs.json.{JsValue, Json}

case class EmergencyContactShape(
  personId: Int,
  emerg1Name: Option[String],
  emerg1Relation: Option[String],
  emerg1PhonePrimary: Option[String],
  emerg1PhonePrimaryType: Option[String],
  emerg1PhoneAlternate: Option[String],
  emerg1PhoneAlternateType: Option[String],

  emerg2Name: Option[String],
  emerg2Relation: Option[String],
  emerg2PhonePrimary: Option[String],
  emerg2PhonePrimaryType: Option[String],
  emerg2PhoneAlternate: Option[String],
  emerg2PhoneAlternateType: Option[String]
)

object EmergencyContactShape {
  implicit val format = Json.format[EmergencyContactShape]
  def apply(v: JsValue): EmergencyContactShape = v.as[EmergencyContactShape]
}
