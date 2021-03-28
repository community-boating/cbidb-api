package org.sailcbi.APIServer.Api.Endpoints.Public

import com.coleji.framework.IO.PreparedQueries.PreparedQueryForSelect
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.UserTypes.PublicRequestCache
import org.sailcbi.APIServer.Services.{PermissionsAuthority, ResultSetWrapper}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetDonationFunds @Inject()(implicit val exec: ExecutionContext) extends InjectedController {
	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		val parsedRequest = ParsedRequest(req)
		PA.withRequestCache(PublicRequestCache)(None, parsedRequest, rc => {


			val q = new PreparedQueryForSelect[DonationFund](Set(PublicRequestCache)) {
				override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): DonationFund = DonationFund(
					fundId = rsw.getInt(2),
					fundName = rsw.getString(1),
					portalDescription = rsw.getString(3),
					isEndowment = rsw.getOptionBooleanFromChar(4).getOrElse(false)
				)

				override def getQuery: String =
					"""
					  |select fund_name d, fund_id r, portal_description, is_endowment from donation_funds f
					  |where active = 'Y' and show_in_checkout = 'Y'
					  |order by display_order
					  |""".stripMargin
			}
			val funds = rc.executePreparedQueryForSelect(q)
			implicit val format = DonationFund.format
			Future(Ok(Json.toJson(funds)))
		})
	})

	case class DonationFund(
		fundId: Int,
		fundName: String,
		portalDescription: String,
		isEndowment: Boolean
	)

	object DonationFund {

		implicit val format = Json.format[DonationFund]

		def apply(v: JsValue): DonationFund = v.as[DonationFund]
	}
}
