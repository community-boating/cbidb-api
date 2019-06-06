package Api.Endpoints.Member

import play.api.libs.json.{JsValue, Json}


case class ScholarshipYesShape(
	personId: Int,
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

case class ScholarshipNoShape(
	personId: Int,
)

object ScholarshipNoShape {
	implicit val format = Json.format[ScholarshipNoShape]

	def apply(v: JsValue): ScholarshipNoShape = v.as[ScholarshipNoShape]
}
