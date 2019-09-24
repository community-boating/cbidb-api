package org.sailcbi.APIServer.Api.Endpoints.Member

import java.sql.ResultSet

import javax.inject.Inject
import org.sailcbi.APIServer.Api.{AuthenticatedRequest, ResultError}
import org.sailcbi.APIServer.CbiUtil.{ParsedRequest, Profiler}
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
			val hasEIIResponse = pb.executePreparedQueryForSelect(new PreparedQueryForSelect[Int](Set(MemberUserType)) {
				override def getQuery: String = "select 1 from eii_responses where person_id = ? and season = util_pkg.get_current_season"

				override val params: List[String] = List(personId.toString)

				override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): Int = 1
			}).nonEmpty
			val childData = pb.executePreparedQueryForSelect(new GetChildDataQuery(personId))
			profiler.lap("got child data")

			val result = WelcomePackageResult(personId, rc.auth.userName, hasEIIResponse, childData)
			implicit val format = WelcomePackageResult.format
			profiler.lap("finishing welcome pkg")
			Future(Ok(Json.toJson(result)))
		}
	})

	case class WelcomePackageResult(
		parentPersonId: Int,
		userName: String,
		hasEIIResponse: Boolean,
		children: List[GetChildDataQueryResult]
	)

	object WelcomePackageResult {
		implicit val format = Json.format[WelcomePackageResult]
	}
}
