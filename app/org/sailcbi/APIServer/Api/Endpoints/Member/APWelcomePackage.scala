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
			val nameQ = new PreparedQueryForSelect[(String, String, LocalDateTime, Int, String, Int, String)](Set(MemberUserType)) {
				override val params: List[String] = List(personId.toString)

				override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): (String, String, LocalDateTime, Int, String, Int, String) =
					(rsw.getString(1), rsw.getString(2), rsw.getLocalDateTime(3), rsw.getInt(4), rsw.getString(5), rsw.getString(6).toInt, rsw.getString(7))

				override def getQuery: String =
					"""
					  |select name_first,
					  |name_last,
					  |util_pkg.get_sysdate,
					  |util_pkg.get_current_season,
					  |person_pkg.ap_status(person_id) as status,
					  |person_pkg.ap_actions(person_id, 'Y') as actions,
					  |ratings_pkg.ap_ratings(person_id)
					  |from persons where person_id = ?
					  |""".stripMargin
			}
			val (nameFirst, nameLast, sysdate, season, status, actions, ratings) = pb.executePreparedQueryForSelect(nameQ).head

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
				canCheckout
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
		canCheckout: Boolean
	)

	object APWelcomePackageResult {
		implicit val format = Json.format[APWelcomePackageResult]
	}
}