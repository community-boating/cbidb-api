package org.sailcbi.APIServer.Api.Endpoints.Member

import java.time.LocalDateTime

import javax.inject.Inject
import org.sailcbi.APIServer.Api.{AuthenticatedRequest, ResultError}
import org.sailcbi.APIServer.CbiUtil.{ParsedRequest, Profiler}
import org.sailcbi.APIServer.IO.Junior.JPPortal
import org.sailcbi.APIServer.IO.PreparedQueries.Member.{GetChildDataQuery, GetChildDataQueryResult}
import org.sailcbi.APIServer.IO.PreparedQueries.PreparedQueryForSelect
import org.sailcbi.APIServer.Services.Authentication.MemberUserType
import org.sailcbi.APIServer.Services.{PermissionsAuthority, ResultSetWrapper}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.{ExecutionContext, Future}

class WelcomePackage @Inject()(implicit val exec: ExecutionContext) extends AuthenticatedRequest {
	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		val profiler = new Profiler
		val logger = PA.logger
		val maybeRC = getRCOptionMember(ParsedRequest(req))
		if (maybeRC.isEmpty) Future {
			Ok(ResultError.UNAUTHORIZED)
		} else {
			val rc = maybeRC.get
			val pb = rc.pb
			profiler.lap("about to do first query")
			val personId = MemberUserType.getAuthedPersonId(rc.auth.userName, pb)
			profiler.lap("got person id")
			val orderId = JPPortal.getOrderId(pb, personId)
			val nameQ = new PreparedQueryForSelect[(String, String, LocalDateTime, Int)](Set(MemberUserType)) {
				override val params: List[String] = List(personId.toString)

				override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): (String, String, LocalDateTime, Int) =
					(rsw.getString(1), rsw.getString(2), rsw.getLocalDateTime(3), rsw.getInt(4))

				override def getQuery: String =
					"""
					  |select name_first, name_last, util_pkg.get_sysdate, util_pkg.get_current_season from persons where person_id = ?
					  |""".stripMargin
			}
			val (nameFirst, nameLast, sysdate, season) = pb.executePreparedQueryForSelect(nameQ).head
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

			val result = WelcomePackageResult(personId, orderId, nameFirst, nameLast, rc.auth.userName, pricesMaybe.map(_._1), pricesMaybe.map(_._2), childData, sysdate, season)
			implicit val format = WelcomePackageResult.format
			profiler.lap("finishing welcome pkg")
			Future(Ok(Json.toJson(result)))
		}
	})

	case class WelcomePackageResult(
		parentPersonId: Int,
		orderId: Int,
		parentFirstName: String,
		parentLastName: String,
		userName: String,
		jpPrice: Option[Double],
		jpOffseasonPrice: Option[Double],
		children: List[GetChildDataQueryResult],
		serverTime: LocalDateTime,
		season: Int
	)

	object WelcomePackageResult {
		implicit val format = Json.format[WelcomePackageResult]
	}
}
