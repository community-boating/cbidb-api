package org.sailcbi.APIServer.Api.Endpoints.Member

import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import com.coleji.neptune.IO.PreparedQueries.PreparedQueryForSelect
import com.coleji.neptune.Storable.ResultSetWrapper
import com.coleji.neptune.Util.{NetFailure, NetSuccess, Profiler}
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.IO.PreparedQueries.Member.{GetChildDataQuery, GetChildDataQueryResult}
import org.sailcbi.APIServer.UserTypes.MemberRequestCache
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent, InjectedController}

import java.time.LocalDateTime
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class JPWelcomePackage @Inject()(ws: WSClient)(implicit val exec: ExecutionContext) extends InjectedController {
	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		val profiler = new Profiler
		val logger = PA.logger
		PA.withRequestCache(MemberRequestCache)(None, ParsedRequest(req), rc => {
			val stripe = rc.getStripeIOController(ws)
			profiler.lap("about to do first query")
			val personId = rc.getAuthedPersonId
			profiler.lap("got person id")
			val orderId = PortalLogic.getOrderIdJP(rc, personId)
			PortalLogic.assessDiscounts(rc, orderId)
			val nameQ = new PreparedQueryForSelect[(String, String, LocalDateTime, Int, Double, Double, Option[String])](Set(MemberRequestCache)) {
				override val params: List[String] = List(personId.toString)

				override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): (String, String, LocalDateTime, Int, Double, Double, Option[String]) =
					(rsw.getString(1), rsw.getString(2), rsw.getLocalDateTime(3), rsw.getInt(4), rsw.getDouble(5), rsw.getDouble(6), rsw.getOptionString(7))

				override def getQuery: String =
					"""
					  |select name_first, name_last, util_pkg.get_sysdate, util_pkg.get_current_season,
					  |person_pkg.get_computed_jp_price(null), person_pkg.get_jp_offseason_price(null),
					  |stripe_customer_id
					  |from persons where person_id = ?
					  |""".stripMargin
			}
			val (nameFirst, nameLast, sysdate, season, jpPriceBase, jpOffseasonPriceBase, stripeCustomerIdOption) = rc.executePreparedQueryForSelect(nameQ).head
			val pricesMaybe = rc.executePreparedQueryForSelect(new PreparedQueryForSelect[(Double, Double)](Set(MemberRequestCache)) {
				override def getQuery: String = """
												  |select
												  | person_pkg.get_computed_jp_price(person_id),
												  | person_pkg.get_jp_offseason_price(person_id)
												  | from eii_responses
												  |where person_id = ? and season = util_pkg.get_current_season and is_current = 'Y'
					""".stripMargin

				override val params: List[String] = List(personId.toString)

				override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): (Double, Double) =
					(rsw.getDouble(1), rsw.getDouble(2))

			}).headOption
			val childData = rc.executePreparedQueryForSelect(new GetChildDataQuery(rc, personId))
			profiler.lap("got child data")

			val canCheckout = PortalLogic.canCheckout(rc, personId, orderId)

			// do this async, user doesnt need to wait for it.
			if (stripeCustomerIdOption.isEmpty) {
				stripe.createStripeCustomerFromPerson(rc, personId).map({
					case f: NetFailure[_, _] => logger.error("Failed to create stripe customerId for person " + personId)
					case s: NetSuccess[_, _] =>
				})
			}

			val result = JPWelcomePackageResult(
				personId,
				orderId,
				nameFirst,
				nameLast,
				rc.userName,
				jpPriceBase,
				jpOffseasonPriceBase,
				pricesMaybe.map(_._1),
				pricesMaybe.map(_._2),
				childData,
				sysdate,
				season,
				canCheckout
			)
			implicit val format = JPWelcomePackageResult.format
			profiler.lap("finishing welcome pkg")
			Future(Ok(Json.toJson(result)))
		})
	})

	case class JPWelcomePackageResult(
		parentPersonId: Int,
		orderId: Int,
		parentFirstName: String,
		parentLastName: String,
		userName: String,
		jpPriceBase: Double,
		jpOffseasonPriceBase: Double,
		jpPrice: Option[Double],
		jpOffseasonPrice: Option[Double],
		children: List[GetChildDataQueryResult],
		serverTime: LocalDateTime,
		season: Int,
		canCheckout: Boolean
	)

	object JPWelcomePackageResult {
		implicit val format = Json.format[JPWelcomePackageResult]
	}
}
