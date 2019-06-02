package Api.Endpoints.Member

import java.sql.ResultSet

import CbiUtil.ParsedRequest
import IO.PreparedQueries.{PreparedQueryForInsert, PreparedQueryForSelect, PreparedQueryForUpdateOrDelete}
import Services.Authentication.MemberUserType
import Services.PermissionsAuthority.UnauthorizedAccessException
import Services.{CacheBroker, PermissionsAuthority, PersistenceBroker, RequestCache}
import javax.inject.Inject
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.ExecutionContext

class Scholarship @Inject()(implicit exec: ExecutionContext) extends Controller {
	def post() = Action { request =>
		try {
			val parsedRequest = ParsedRequest(request)
			val juniorId: Option[Int] = request.body.asJson.map(json => json("personId").toString().toInt)
			val rc: RequestCache = PermissionsAuthority.getRequestCacheMember(None, parsedRequest, juniorId)._2.get
			val pb: PersistenceBroker = rc.pb
			val cb: CacheBroker = rc.cb
			val data = request.body.asJson
			data match {
				case None => {
					println("no body")
					new Status(400)("no body")
				}
				case Some(v: JsValue) => {
					val parsed = ScholarshipShape.apply(v)

					val insertQuery = new PreparedQueryForInsert(Set(MemberUserType)) {
						val params: List[String] = List(parsed.personId.toString)
						override val pkName: Option[String] = None

						override def getQuery: String =
							s"""
							   |insert into eii_responses
							   |(person_id, season, is_current) values
							   | (?, 2019, 'Y')
							 """.stripMargin
					}

					pb.executePreparedQueryForInsert(insertQuery)

					Ok("done")
				}
				case Some(v) => {
					println("wut dis " + v)
					Ok("wat")
				}
			}


			Ok("done")

		} catch {
			case _: UnauthorizedAccessException => Ok("Access Denied")
			case e: Throwable => {
				println(e)
				e.printStackTrace()
				Ok("Internal Error")
			}
		}
	}
}

case class ScholarshipShape(personId: Int)

object ScholarshipShape {
	implicit val format = Json.format[ScholarshipShape]

	def apply(v: JsValue): ScholarshipShape = v.as[ScholarshipShape]
}
