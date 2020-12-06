package org.sailcbi.APIServer.Api.Endpoints.Member

import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.PreparedQueries.{PreparedQueryForSelect, PreparedQueryForUpdateOrDelete}
import org.sailcbi.APIServer.Services.Authentication.MemberUserType
import org.sailcbi.APIServer.Services._
import play.api.libs.json.{JsBoolean, JsObject, JsValue, Json}
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SwimProof @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def get(juniorId: Int)(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCacheMemberWithJuniorId(None, parsedRequest, juniorId, rc => {
			val pb: PersistenceBroker = rc.pb
			val cb: CacheBroker = rc.cb

			val select = new PreparedQueryForSelect[SwimProofShape](Set(MemberUserType)) {
				override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): SwimProofShape =
					SwimProofShape(
						juniorId,
						rs.getOptionInt(1).map(_.toString)
					)

				override def getQuery: String =
					s"""
					   |select
					   |swim_proof
					   |from persons where person_id = ?
        """.stripMargin

				override val params: List[String] = List(juniorId.toString)
			}

			val resultObj = pb.executePreparedQueryForSelect(select).head
			val resultJson: JsValue = Json.toJson(resultObj)
			Future(Ok(resultJson))
		})

	}

	def post()(implicit PA: PermissionsAuthority) = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		val juniorId: Int = request.body.asJson.map(json => json("personId").toString().toInt).get
		PA.withRequestCacheMemberWithJuniorId(None, parsedRequest, juniorId, rc => {
			val pb: PersistenceBroker = rc.pb
			val cb: CacheBroker = rc.cb
			val data = request.body.asJson
			PA.withParsedPostBodyJSON(request.body.asJson, SwimProofShape.apply)(parsed => {
				val updateQuery = new PreparedQueryForUpdateOrDelete(Set(MemberUserType)) {
					override def getQuery: String =
						s"""
						   |update persons set
						   |swim_proof = ?
						   |where person_id = ?
		  """.stripMargin

					override val params: List[String] = List(
						parsed.swimProofId.map(_.toString).orNull,
						parsed.personId.toString
					)
				}

				pb.executePreparedQueryForUpdateOrDelete(updateQuery)

				Future(Ok(JsObject(Map("success" -> JsBoolean(true)))))
			})
		})
	}
}
