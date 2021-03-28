package org.sailcbi.APIServer.Api.Endpoints.Public

import com.coleji.framework.Core.PermissionsAuthority
import com.coleji.framework.IO.PreparedQueries.PreparedQueryForSelect
import com.coleji.framework.Storable.ResultSetWrapper
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.UserTypes.PublicRequestCache
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PingDB @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCache(PublicRequestCache)(None, parsedRequest, rc => {


			val q = new PreparedQueryForSelect[Int](Set(PublicRequestCache)) {
				override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): Int = rs.getInt(1)

				override def getQuery: String =
					s"""
					   |select count(*) from boat_types
        """.stripMargin

				override val params: List[String] = List.empty
			}

			val ct = rc.executePreparedQueryForSelect(q).head
			Future(Ok(ct.toString))
		})

	}
}
