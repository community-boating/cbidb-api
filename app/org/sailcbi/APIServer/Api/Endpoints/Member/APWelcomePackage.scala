package org.sailcbi.APIServer.Api.Endpoints.Member

import org.sailcbi.APIServer.CbiUtil._
import org.sailcbi.APIServer.Entities.MagicIds
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.IO.PreparedQueries.PreparedQueryForSelect
import org.sailcbi.APIServer.Services.Authentication.MemberRequestCache
import org.sailcbi.APIServer.Services.{PermissionsAuthority, ResultSetWrapper}
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent, InjectedController}

import java.time.{LocalDate, LocalDateTime}
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class APWelcomePackage @Inject()(ws: WSClient)(implicit val exec: ExecutionContext) extends InjectedController {
	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		val profiler = new Profiler
		val logger = PA.logger
		PA.withRequestCacheMember(None, ParsedRequest(req), rc => {
			val stripe = rc.getStripeIOController(ws)
			profiler.lap("about to do first query")
			val personId = rc.getAuthedPersonId(rc)
			profiler.lap("got person id")
			val orderId = PortalLogic.getOrderIdAP(rc, personId)
			PortalLogic.assessDiscounts(rc, orderId)
			type ResultUntyped = (
				String, String, LocalDateTime, Int, String, Int, String, Boolean, Boolean, Boolean, Boolean, Boolean, Boolean, Boolean, Boolean, Boolean, Option[String]
			)
			val nameQ = new PreparedQueryForSelect[ResultUntyped](Set(MemberRequestCache)) {
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
					rsw.getBooleanFromChar(14),
					rsw.getBooleanFromChar(15),
					rsw.getBooleanFromChar(16),
					rsw.getOptionString(17)
				)

				override def getQuery: String =
					s"""
					  |select
					  |name_first,
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
					  |person_pkg.can_renew(?),
					  |(case when dob is not null and util_pkg.age(dob,util_pkg.get_sysdate) >= ${MagicIds.MIN_AGE_FOR_SENIOR} then 'Y' else 'N' end) as senior_available,
					  |(case when dob is not null and util_pkg.age(dob,util_pkg.get_sysdate) between 18 and ${MagicIds.MAX_AGE_FOR_YOUTH} then 'Y' else 'N' end) as youth_available,
					  |stripe_customer_id
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
				canRenew,
				seniorAvailable,
				youthAvailable,
				stripeCustomerIdOption
			) = rc.executePreparedQueryForSelect(nameQ).head

			// do this async, user doesnt need to wait for it.
			if (stripeCustomerIdOption.isEmpty) {
				stripe.createStripeCustomerFromPerson(rc, personId).map({
					case f: NetFailure[_, _] => logger.error("Failed to create stripe customerId for person " + personId)
					case s: NetSuccess[_, _] =>
				})
			}

			val discountsWithAmounts = PortalLogic.getDiscountsWithAmounts(rc)
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
					val (_, expirationDate) = PortalLogic.getFYExpirationDate(rc, personId)
					Some(expirationDate)
				} else {
					None
				}
			}

			val canCheckout = PortalLogic.canCheckout(rc, personId, orderId)

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
				fyBasePrice = fyBasePrice,
				seniorAvailable = seniorAvailable,
				youthAvailable = youthAvailable
			)

			val result: APWelcomePackageResult = APWelcomePackageResult(
				personId = personId,
				orderId = orderId,
				firstName = nameFirst,
				lastName = nameLast,
				userName = rc.userName,
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
		seniorAvailable: Boolean,
		youthAvailable: Boolean,
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

