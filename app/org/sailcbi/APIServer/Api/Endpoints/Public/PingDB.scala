package org.sailcbi.APIServer.Api.Endpoints.Public

import javax.inject.Inject
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.PreparedQueries.PreparedQueryForSelect
import org.sailcbi.APIServer.Services.Authentication.PublicUserType
import org.sailcbi.APIServer.Services.{PermissionsAuthority, PersistenceBroker, RequestCache, ResultSetWrapper}
import play.api.mvc.{Action, AnyContent, InjectedController}

import scala.concurrent.ExecutionContext

class PingDB @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action { request =>
		val parsedRequest = ParsedRequest(request)
		val rc: RequestCache = PA.getRequestCache(PublicUserType, None, parsedRequest).get
		val pb: PersistenceBroker = rc.pb

		val q = new PreparedQueryForSelect[Int](Set(PublicUserType)) {
			override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): Int = rs.getInt(1)

			override def getQuery: String =
				s"""
				   |select count(*) from boat_types
        """.stripMargin

			override val params: List[String] = List.empty
		}

		val ct = pb.executePreparedQueryForSelect(q).head
		Ok(ct.toString)
	}
}
