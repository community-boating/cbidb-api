package org.sailcbi.APIServer.Api.Endpoints.Member

import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import com.coleji.neptune.IO.PreparedQueries.PreparedQueryForSelect
import com.coleji.neptune.Storable.ResultSetWrapper
import org.sailcbi.APIServer.Entities.MagicIds
import org.sailcbi.APIServer.UserTypes.MemberRequestCache
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent, InjectedController}

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetRecurringDonationHistory @Inject()(implicit exec: ExecutionContext, ws: WSClient) extends InjectedController {
	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCache(MemberRequestCache)(None, parsedRequest, rc => {
			val personId = rc.getAuthedPersonId

			val q = new PreparedQueryForSelect[Option[LocalDate]](Set(MemberRequestCache)) {
				override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Option[LocalDate] = rsw.getOptionLocalDate(1)

				override def getQuery: String = s"select NEXT_RECURRING_DONATION from persons where person_id = $personId"
			}
			val nextDonation = rc.executePreparedQueryForSelect(q).head

			val newHistoryQ = new PreparedQueryForSelect[DonationRecord](Set(MemberRequestCache)) {
				override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): DonationRecord = DonationRecord(
					orderId = rsw.getInt(1),
					donatedDate = rsw.getLocalDate(2),
					fundId = rsw.getInt(3),
					amount = rsw.getDouble(4)
				)

				override val params: List[String] = List(
					personId.toString,
					"DONATE_REC"
				)

				override def getQuery: String =
					s"""
					  |select d.compass_order_id, d.donation_date, d.fund_id, d.amount from donations d, compass_order o
					  |where d.compass_order_id = o.compass_order_id and d.person_id = ?
					  |and o.order_type = ?
					  |""".stripMargin
			}

			val newHistory = rc.executePreparedQueryForSelect(newHistoryQ)

			val historyQ = new PreparedQueryForSelect[DonationRecord](Set(MemberRequestCache)) {
				override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): DonationRecord = DonationRecord(
					orderId = rsw.getInt(1),
					donatedDate = rsw.getLocalDate(2),
					fundId = rsw.getInt(3),
					amount = rsw.getDouble(4)
				)

				override val params: List[String] = List(
					personId.toString,
					MagicIds.ORDER_NUMBER_APP_ALIAS.AUTO_DONATE
				)

				override def getQuery: String =
					s"""
					  |select d.order_id, donation_date, fund_id, amount from donations d, order_numbers o
					  |where d.order_id = o.order_id and d.person_id = ?
					  | and (o.app_alias = ? or o.use_payment_intent = 'Y')
					  | order by donation_date, fund_id
					  |""".stripMargin
			}

			val history = rc.executePreparedQueryForSelect(historyQ)
			implicit val historyFormat = DonationRecord.format
			implicit val format = RecurringDonationHistoryShape.format

			Future(Ok(Json.toJson(RecurringDonationHistoryShape(nextChargeDate = nextDonation, donationHistory = history ::: newHistory))))
		})
	}

	case class DonationRecord(orderId: Int, donatedDate: LocalDate, fundId: Int, amount: Double)
	object DonationRecord {
		implicit val format = Json.format[DonationRecord]
		def apply(v: JsValue): DonationRecord = v.as[DonationRecord]
	}

	case class RecurringDonationHistoryShape(nextChargeDate: Option[LocalDate], donationHistory: List[DonationRecord])
	object RecurringDonationHistoryShape {
		implicit val format = Json.format[RecurringDonationHistoryShape]
		def apply(v: JsValue): RecurringDonationHistoryShape = v.as[RecurringDonationHistoryShape]
	}
}
