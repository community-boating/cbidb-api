package org.sailcbi.APIServer.Api.Endpoints.Member

import java.time.{LocalDateTime, LocalDate}

import javax.inject.Inject
import org.sailcbi.APIServer.CbiUtil.{BitVector, ParsedRequest, Profiler}
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.IO.PreparedQueries.Member.{GetChildDataQuery, GetChildDataQueryResult}
import org.sailcbi.APIServer.IO.PreparedQueries.PreparedQueryForSelect
import org.sailcbi.APIServer.Services.Authentication.MemberUserType
import org.sailcbi.APIServer.Services.{PermissionsAuthority, ResultSetWrapper}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, InjectedController}

import scala.concurrent.{ExecutionContext, Future}

class APWelcomePackage @Inject()(implicit val exec: ExecutionContext) extends InjectedController {
	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		val profiler = new Profiler
		val logger = PA.logger
		PA.withRequestCacheMember(None, ParsedRequest(req), rc => {
			val pb = rc.pb
			profiler.lap("about to do first query")
			val personId = MemberUserType.getAuthedPersonId(rc.auth.userName, pb)
			profiler.lap("got person id")
			val orderId = PortalLogic.getOrderId(pb, personId)
			PortalLogic.assessDiscounts(pb, orderId)
			type ResultUntyped = (
				String, String, LocalDateTime, Int, String, Int, String, Boolean, Boolean, Boolean, Boolean, Boolean
			)
			val nameQ = new PreparedQueryForSelect[ResultUntyped](Set(MemberUserType)) {
				override val params: List[String] = List(personId.toString, personId.toString, personId.toString, personId.toString, personId.toString)

				override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): ResultUntyped = (
					rsw.getString(1),
					rsw.getString(2),
					rsw.getLocalDateTime(3),
					rsw.getInt(4),
					rsw.getString(5),
					rsw.getString(6).toInt,
					rsw.getString(7),
					rsw.getBooleanFromChar(8),
					rsw.getBooleanFromChar(9),
					rsw.getBooleanFromChar(10),
					rsw.getBooleanFromChar(11),
					rsw.getBooleanFromChar(12)
				)

				override def getQuery: String =
					"""
					  |select name_first,
					  |name_last,
					  |util_pkg.get_sysdate,
					  |util_pkg.get_current_season,
					  |person_pkg.ap_status(person_id) as status,
					  |person_pkg.ap_actions(person_id, 'Y') as actions,
					  |ratings_pkg.ap_ratings(person_id),
						|check_yearly_date('4TH_PORTAL_LINKS'),
						|person_pkg.eligible_for_senior_online(?),
						|person_pkg.eligible_for_youth_online(?),
						|person_pkg.eligible_for_veteran_online(?),
						|person_pkg.can_renew(?)
					  |from persons where person_id = ?
					  |""".stripMargin
			}
			val (
				nameFirst,
				nameLast,
				sysdate,
				season,
				status,
				actions,
				ratings,
				show4thLink,
				eligibleForSeniorOnline,
				eligibleForYouthOnline,
				eligibleForVeteranOnline,
				canRenew
			) = pb.executePreparedQueryForSelect(nameQ).head

			val (renewalDiscountAmt, _) = PortalLogic.getFYRenewalDiscountAmount(pb)


			val expirationDate = {
				if (BitVector.testBit(actions, 4) || BitVector.testBit(actions, 7)) {
					val (_, expirationDate) = PortalLogic.getFYExpirationDate(pb, personId)
					Some(expirationDate)
				} else {
					None
				}
			}

			val canCheckout = PortalLogic.canCheckout(pb, personId, orderId)

			val result = APWelcomePackageResult(
				personId,
				orderId,
				nameFirst,
				nameLast,
				rc.auth.userName,
				sysdate,
				season,
				status,
				actions,
				ratings,
				canCheckout,
				renewalDiscountAmt,
				expirationDate,
				show4thLink,
				eligibleForSeniorOnline,
				eligibleForYouthOnline,
				eligibleForVeteranOnline,
				canRenew
			)
			implicit val format = APWelcomePackageResult.format
			profiler.lap("finishing welcome pkg")
			Future(Ok(Json.toJson(result)))
		})
	})

	case class APWelcomePackageResult(
		personId: Int,
		orderId: Int,
		firstName: String,
		lastName: String,
		userName: String,
		serverTime: LocalDateTime,
		season: Int,
		status: String,
		actions: Int,
		ratings: String,
		canCheckout: Boolean,
		renewalDiscountAmt: Double,
		expirationDate: Option[LocalDate],
		show4thLink: Boolean,
		eligibleForSeniorOnline: Boolean,
		eligibleForYouthOnline: Boolean,
		eligibleForVeteranOnline: Boolean,
		canRenew: Boolean
	)

	object APWelcomePackageResult {
		implicit val format = Json.format[APWelcomePackageResult]
	}
}