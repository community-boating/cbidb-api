package org.sailcbi.APIServer.Api.Endpoints.Member

import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.PreparedQueries.PreparedQueryForSelect
import org.sailcbi.APIServer.Services.Authentication.MemberRequestCache
import org.sailcbi.APIServer.Services._
import play.api.libs.json.{JsArray, JsBoolean, JsNumber, JsObject}
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class JuniorSeeClassTypes @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	case class SeeTypeResult(typeId: Int, canSee: Boolean)

	def get(juniorId: Int)(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCacheMemberWithJuniorId(parsedRequest, juniorId, rc => {
			val select = new PreparedQueryForSelect[SeeTypeResult](Set(MemberRequestCache)) {
				override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): SeeTypeResult =
					SeeTypeResult(rs.getInt(1), rs.getBooleanFromChar(2))

				// Limiting to class types in current use reduces query time from ~1200ms to ~900ms
				override def getQuery: String =
					s"""
					   |select type_id, jp_class_pkg.see_type(?, type_id)
					   |from (
					   |    select distinct i.type_id from jp_class_instances i, jp_class_bookends bk, jp_class_sessions fs
					   |    where i.instance_id = bk.instance_id and bk.first_session = fs.session_id
					   |    and to_char(fs.session_datetime, 'YYYY') = util_pkg.get_current_season
					   |)
				 """.stripMargin

				override val params: List[String] = List(juniorId.toString)
			}

			val arr = JsArray(
				rc.executePreparedQueryForSelect(select)
						.map(t => JsObject(Map(
							"typeId" -> JsNumber(t.typeId),
							"canSee" -> JsBoolean(t.canSee)
						)))
			)

			Future(Ok(arr))
		})
	}
}