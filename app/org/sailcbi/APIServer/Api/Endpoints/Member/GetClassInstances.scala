package org.sailcbi.APIServer.Api.Endpoints.Member

import com.coleji.framework.IO.PreparedQueries.PreparedQueryForSelect
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.IO.PreparedQueries.Member.{GetClassInstancesQuery, GetClassInstancesQueryResult}
import org.sailcbi.APIServer.UserTypes.MemberRequestCache
import org.sailcbi.APIServer.Services.{PermissionsAuthority, ResultSetWrapper}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetClassInstances @Inject()(implicit val exec: ExecutionContext) extends InjectedController {
	def junior(typeId: Int, juniorId: Int)(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		val parsedRequest = ParsedRequest(req)
		PA.withRequestCacheMemberWithJuniorId(parsedRequest, juniorId, rc => {
			PortalLogic.pruneOldReservations(rc)

			val instances = rc.executePreparedQueryForSelect(GetClassInstancesQuery.byJunior(None, typeId, juniorId)).toArray
			val classInfoQuery = new PreparedQueryForSelect[ClassTypeInfo](Set(MemberRequestCache)) {
				override val params: List[String] = List(typeId.toString)

				override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): ClassTypeInfo = ClassTypeInfo(
					typeId,
					rsw.getString(1),
					rsw.getDouble(2),
					rsw.getInt(3),
					instances
				)

				override def getQuery: String =
					"""
					  |select type_name, session_length, session_ct from jp_class_types where type_id = ?
					  |""".stripMargin
			}
			val classInfo = rc.executePreparedQueryForSelect(classInfoQuery).head
			implicit val format = ClassTypeInfo.format
			Future(Ok(Json.toJson(classInfo)))
		})
	})

	case class ClassTypeInfo(typeId: Int, typeName: String, sessionLength: Double, sessionCt: Int, instances: Array[GetClassInstancesQueryResult])

	object ClassTypeInfo {

		implicit val format = Json.format[ClassTypeInfo]

		def apply(v: JsValue): ClassTypeInfo = v.as[ClassTypeInfo]
	}
}
