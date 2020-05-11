package org.sailcbi.APIServer.Api.Endpoints.Member

import java.time.{LocalDate, LocalDateTime}

import javax.inject.Inject
import org.sailcbi.APIServer.CbiUtil.{BitVector, ParsedRequest, Profiler}
import org.sailcbi.APIServer.Entities.MagicIds
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.IO.Portal.PortalLogic.DiscountWithAmount
import org.sailcbi.APIServer.IO.PreparedQueries.Member.{GetChildDataQuery, GetChildDataQueryResult}
import org.sailcbi.APIServer.IO.PreparedQueries.PreparedQueryForSelect
import org.sailcbi.APIServer.Services.Authentication.MemberUserType
import org.sailcbi.APIServer.Services.{PermissionsAuthority, ResultSetWrapper}
import play.api.libs.json.{JsValue, Json}
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
				String, String, LocalDateTime, Int, String, Int, String, Boolean, Boolean, Boolean, Boolean, Boolean, Boolean, Boolean
			)
			val nameQ = new PreparedQueryForSelect[ResultUntyped](Set(MemberUserType)) {
				override val params: List[String] = List(personId.toString, personId.toString, personId.toString, personId.toString, personId.toString, personId.toString, personId.toString)

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
					rsw.getBooleanFromChar(12),
					rsw.getBooleanFromChar(13),
					rsw.getBooleanFromChar(14)
				)

				override def getQuery: String =
					s"""
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
					  |(case when exists (
					  |    select 1 from persons_discounts_eligible
					  |      where person_id = ? and discount_id = ${MagicIds.DISCOUNTS.STUDENT_DISCOUNT_ID} and season = util_pkg.get_current_season
					  |) then 'Y' else 'N' end) as student_eligible,
					  |(case when exists (
					  |    select 1 from persons_discounts_eligible
					  |      where person_id = ? and discount_id = ${MagicIds.DISCOUNTS.MGH_DISCOUNT_ID} and season = util_pkg.get_current_season
					  |) then 'Y' else 'N' end) as mgh_eligible,
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
				eligibleForStudent,
				eligibleForMGH,
				canRenew
			) = pb.executePreparedQueryForSelect(nameQ).head

			val discountsWithAmounts = PortalLogic.getDiscountsWithAmounts(pb)
			val fullYearDiscounts = discountsWithAmounts.filter(_.membershipTypeId == MagicIds.MEMBERSHIP_TYPES.FULL_YEAR_TYPE_ID)
			val renewalDiscountAmt = fullYearDiscounts.find(_.discountId == MagicIds.DISCOUNTS.RENEWAL_DISCOUNT_ID ).get.discountAmount
			val seniorDiscountAmt = fullYearDiscounts.find(_.discountId == MagicIds.DISCOUNTS.SENIOR_DISCOUNT_ID ).get.discountAmount
			val youthDiscountAmt = fullYearDiscounts.find(_.discountId == MagicIds.DISCOUNTS.YOUTH_DISCOUNT_ID ).get.discountAmount
			val studentDiscountAmt = fullYearDiscounts.find(_.discountId == MagicIds.DISCOUNTS.STUDENT_DISCOUNT_ID ).get.discountAmount
			val veteranDiscountAmt = fullYearDiscounts.find(_.discountId == MagicIds.DISCOUNTS.VETERAN_DISCOUNT_ID ).get.discountAmount
			val mghDiscountAmt = fullYearDiscounts.find(_.discountId == MagicIds.DISCOUNTS.MGH_DISCOUNT_ID ).get.discountAmount
			val fyBasePrice = fullYearDiscounts.head.fullPrice

			val expirationDate = {
				if (BitVector.testBit(actions, 4) || BitVector.testBit(actions, 7)) {
					val (_, expirationDate) = PortalLogic.getFYExpirationDate(pb, personId)
					Some(expirationDate)
				} else {
					None
				}
			}

			val canCheckout = PortalLogic.canCheckout(pb, personId, orderId)

			val discountsResult = DiscountsResult(
				eligibleForSeniorOnline = eligibleForSeniorOnline,
				eligibleForYouthOnline = eligibleForYouthOnline,
				eligibleForVeteranOnline = eligibleForVeteranOnline,
				eligibleForStudent = eligibleForStudent,
				eligibleForMGH = eligibleForMGH,
				canRenew = canRenew,
				renewalDiscountAmt = renewalDiscountAmt,
				seniorDiscountAmt = seniorDiscountAmt,
				youthDiscountAmt = youthDiscountAmt,
				studentDiscountAmt = studentDiscountAmt,
				veteranDiscountAmt = veteranDiscountAmt,
				mghDiscountAmt = mghDiscountAmt,
				fyBasePrice = fyBasePrice
			)

			val result: APWelcomePackageResult = APWelcomePackageResult(
				personId = personId,
				orderId = orderId,
				firstName = nameFirst,
				lastName = nameLast,
				userName = rc.auth.userName,
				serverTime = sysdate,
				season = season,
				status = status,
				actions = actions,
				ratings = ratings,
				canCheckout = canCheckout,
				expirationDate = expirationDate,
				show4thLink = show4thLink,
				discountsResult = discountsResult
			)
			implicit val discountsFormat = DiscountsResult.format
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
		expirationDate: Option[LocalDate],
		show4thLink: Boolean,
		discountsResult: DiscountsResult
	)

	object APWelcomePackageResult {
		implicit val format = Json.format[APWelcomePackageResult]

		def apply(v: JsValue): APWelcomePackageResult = v.as[APWelcomePackageResult]
	}

	case class DiscountsResult (
		eligibleForSeniorOnline: Boolean,
		eligibleForYouthOnline: Boolean,
		eligibleForVeteranOnline: Boolean,
		eligibleForStudent: Boolean,
		eligibleForMGH: Boolean,
		canRenew: Boolean,
		renewalDiscountAmt: Double,
		seniorDiscountAmt: Double,
		youthDiscountAmt: Double,
		studentDiscountAmt: Double,
		veteranDiscountAmt: Double,
		mghDiscountAmt: Double,
		fyBasePrice: Double
	)

	object DiscountsResult {
		implicit val format = Json.format[DiscountsResult]

		def apply(v: JsValue): DiscountsResult = v.as[DiscountsResult]
	}
}

