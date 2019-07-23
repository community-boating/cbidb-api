package Api.Endpoints.Member

import play.api.libs.json.{JsValue, Json}


case class ScholarshipYesShape(
	numberWorkers: Int,
	hasBenefits: Boolean,
	infantCount: Int,
	preschoolerCount: Int,
	schoolagerCount: Int,
	teenagerCount: Int,
	income: Double
)

object ScholarshipYesShape {
	implicit val format = Json.format[ScholarshipYesShape]

	def apply(v: JsValue): ScholarshipYesShape = v.as[ScholarshipYesShape]
}
