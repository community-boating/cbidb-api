package org.sailcbi.APIServer.Api.Endpoints.Member

import com.coleji.neptune.Core.{CacheBroker, ParsedRequest, PermissionsAuthority}
import com.coleji.neptune.IO.PreparedQueries.{PreparedQueryForSelect, PreparedQueryForUpdateOrDelete}
import com.coleji.neptune.Storable.ResultSetWrapper
import org.sailcbi.APIServer.UserTypes.MemberRequestCache
import play.api.libs.json.{JsBoolean, JsObject, JsValue, Json}
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SwimProof @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def get(juniorId: Int)(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		MemberRequestCache.withRequestCacheMemberWithJuniorId(parsedRequest, juniorId, rc => {
			val cb: CacheBroker = rc.cb

			val select = new PreparedQueryForSelect[SwimProofShape](Set(MemberRequestCache)) {
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

			val resultObj = rc.executePreparedQueryForSelect(select).head
			val resultJson: JsValue = Json.toJson(resultObj)
			Future(Ok(resultJson))
		})

	}

	def post()(implicit PA: PermissionsAuthority) = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		val juniorId: Int = request.body.asJson.map(json => json("personId").toString().toInt).get
		MemberRequestCache.withRequestCacheMemberWithJuniorId(parsedRequest, juniorId, rc => {
			val cb: CacheBroker = rc.cb
			val data = request.body.asJson
			PA.withParsedPostBodyJSON(request.body.asJson, SwimProofShape.apply)(parsed => {
				val updateQuery = new PreparedQueryForUpdateOrDelete(Set(MemberRequestCache)) {
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

				rc.executePreparedQueryForUpdateOrDelete(updateQuery)

				Future(Ok(JsObject(Map("success" -> JsBoolean(true)))))
			})
		})
	}
}
