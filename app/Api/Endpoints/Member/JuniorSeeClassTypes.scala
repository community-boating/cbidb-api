package Api.Endpoints.Member

import java.sql.ResultSet

import CbiUtil.ParsedRequest
import IO.PreparedQueries.PreparedQueryForSelect
import Services.Authentication.MemberUserType
import Services.{CacheBroker, PermissionsAuthority, PersistenceBroker, RequestCache}
import javax.inject.Inject
import play.api.libs.json.{JsArray, JsBoolean, JsNumber, JsObject, JsValue}
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.ExecutionContext

class JuniorSeeClassTypes @Inject()(implicit exec: ExecutionContext) extends Controller {
	case class SeeTypeResult(typeId: Int, canSee: Boolean)

	def get(juniorId: Int): Action[AnyContent] = Action { request =>
		val parsedRequest = ParsedRequest(request)
		val rc: RequestCache = PermissionsAuthority.getRequestCacheMemberWithJuniorId(None, parsedRequest, juniorId)._2.get
		val pb: PersistenceBroker = rc.pb
		val cb: CacheBroker = rc.cb

		val select = new PreparedQueryForSelect[SeeTypeResult](Set(MemberUserType)) {
			override def mapResultSetRowToCaseObject(rs: ResultSet): SeeTypeResult =
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
			pb.executePreparedQueryForSelect(select)
				.map(t => JsObject(Map(
					"typeId" -> JsNumber(t.typeId),
					"canSee" -> JsBoolean(t.canSee)
				)))
		)

		Ok(arr)
	}
}