package Api.Endpoints.Member

import play.api.libs.json.{JsValue, Json}

case class SurveyInfoShape(
	personId: Int,
	genderID: Option[String],
	referral: Option[Array[String]],
	referralOther: Option[String],
	language: Option[String],
	ethnicity: Option[Array[String]],
	ethnicityOther: Option[String],
	school: Option[String],
	freeLunch: Option[Boolean]
)

object SurveyInfoShape {
	implicit val format = Json.format[SurveyInfoShape]

	def apply(v: JsValue): SurveyInfoShape = v.as[SurveyInfoShape]
}
