package org.sailcbi.APIServer.Api.Endpoints.Member

import javax.inject.Inject
import org.sailcbi.APIServer.Api.{ValidationError, ValidationResult}
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.Junior.JPPortal
import org.sailcbi.APIServer.IO.PreparedQueries.PreparedQueryForUpdateOrDelete
import org.sailcbi.APIServer.Services.Authentication.{MemberUserType, ProtoPersonUserType}
import org.sailcbi.APIServer.Services.PermissionsAuthority.UnauthorizedAccessException
import org.sailcbi.APIServer.Services.{PermissionsAuthority, PersistenceBroker, RequestCache}
import play.api.libs.json.{JsNumber, JsObject, JsValue, Json}
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext

class AcceptTOS  @Inject()(implicit exec: ExecutionContext) extends Controller {
	def post()(implicit PA: PermissionsAuthority) = Action { request =>
		try {
			val logger = PA.logger
			val parsedRequest = ParsedRequest(request)
			val rc: RequestCache = PA.getRequestCacheMember(None, parsedRequest)._2.get
			val pb = rc.pb
			val data = request.body.asJson
			data match {
				case None => {
					println("no body")
					new Status(400)("no body")
				}
				case Some(v: JsValue) => {
					println(v)
					val parsed = AcceptTOSShape.apply(v)

					val parentId = MemberUserType.getAuthedPersonId(rc.auth.userName, pb)
					val orderId = JPPortal.getOrderId(pb, parentId)

					val q = new PreparedQueryForUpdateOrDelete(Set(MemberUserType)) {
						override val params: List[String] = List(parsed.personId.toString, orderId.toString)

						override def getQuery: String =
							"""
							  |update shopping_cart_memberships
							  |  set ready_to_buy = 'Y'
							  |  where person_id = ?
							  |  and ready_to_buy = 'N'
							  |  and order_id = ?
							  |""".stripMargin
					}
					pb.executePreparedQueryForUpdateOrDelete(q)
					Ok("done")
				}
				case Some(v) => {
					println("wut dis " + v)
					Ok("wat")
				}
			}
		} catch {
			case _: UnauthorizedAccessException => Ok("Access Denied")
			case e: Throwable => {
				println(e)
				e.printStackTrace()
				Ok("Internal Error")
			}
		}
	}

	case class AcceptTOSShape(personId: Int)

	object AcceptTOSShape {
		implicit val format = Json.format[AcceptTOSShape]

		def apply(v: JsValue): AcceptTOSShape = v.as[AcceptTOSShape]
	}

}
