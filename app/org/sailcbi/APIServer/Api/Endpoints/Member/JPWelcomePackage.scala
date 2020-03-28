package org.sailcbi.APIServer.Api.Endpoints.Member

import java.time.LocalDateTime

import javax.inject.Inject
import org.sailcbi.APIServer.CbiUtil.{ParsedRequest, Profiler}
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.IO.PreparedQueries.Member.{GetChildDataQuery, GetChildDataQueryResult}
import org.sailcbi.APIServer.IO.PreparedQueries.PreparedQueryForSelect
import org.sailcbi.APIServer.Services.Authentication.MemberUserType
import org.sailcbi.APIServer.Services.{PermissionsAuthority, ResultSetWrapper}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, InjectedController}

import scala.concurrent.{ExecutionContext, Future}

class JPWelcomePackage @Inject()(implicit val exec: ExecutionContext) extends InjectedController {
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
			val nameQ = new PreparedQueryForSelect[(String, String, LocalDateTime, Int, Double, Double)](Set(MemberUserType)) {
				override val params: List[String] = List(personId.toString)

				override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): (String, String, LocalDateTime, Int, Double, Double) =
					(rsw.getString(1), rsw.getString(2), rsw.getLocalDateTime(3), rsw.getInt(4), rsw.getDouble(5), rsw.getDouble(6))

				override def getQuery: String =
					"""
					  |select name_first, name_last, util_pkg.get_sysdate, util_pkg.get_current_season,
					  |person_pkg.get_computed_jp_price(null), person_pkg.get_jp_offseason_price(null)
					  |from persons where person_id = ?
					  |""".stripMargin
			}
			val (nameFirst, nameLast, sysdate, season, jpPriceBase, jpOffseasonPriceBase) = pb.executePreparedQueryForSelect(nameQ).head
			val pricesMaybe = pb.executePreparedQueryForSelect(new PreparedQueryForSelect[(Double, Double)](Set(MemberUserType)) {
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
			val childData = pb.executePreparedQueryForSelect(new GetChildDataQuery(personId))
			profiler.lap("got child data")

			val canCheckout = PortalLogic.canCheckout(pb, personId, orderId)

			val result = JPWelcomePackageResult(
				personId,
				orderId,
				nameFirst,
				nameLast,
				rc.auth.userName,
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
