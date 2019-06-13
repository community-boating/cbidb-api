package Api.Endpoints.Member

import java.sql.ResultSet

import CbiUtil.ParsedRequest
import IO.PreparedQueries.{PreparedQueryForSelect, PreparedQueryForUpdateOrDelete}
import Services.Authentication.MemberUserType
import Services.PermissionsAuthority.UnauthorizedAccessException
import Services.{CacheBroker, PermissionsAuthority, PersistenceBroker, RequestCache}
import javax.inject.Inject
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.ExecutionContext

class RequiredInfo @Inject()(implicit exec: ExecutionContext) extends Controller {
	def get(juniorId: Int): Action[AnyContent] = Action { request =>
		val parsedRequest = ParsedRequest(request)
		val rc: RequestCache = PermissionsAuthority.getRequestCacheMemberWithJuniorId(None, parsedRequest, juniorId)._2.get
		val pb: PersistenceBroker = rc.pb
		val cb: CacheBroker = rc.cb

		val select = new PreparedQueryForSelect[RequiredInfoShape](Set(MemberUserType)) {
			override def mapResultSetRowToCaseObject(rs: ResultSet): RequiredInfoShape =
				RequiredInfoShape(
					juniorId,
					rs.getOptionString(1),
					rs.getOptionString(2),
					rs.getOptionString(3),
					rs.getOptionString(4),
					rs.getOptionString(5),
					rs.getOptionString(6),
					rs.getOptionString(7),
					rs.getOptionString(8),
					rs.getOptionString(9),
					rs.getOptionString(10),
					rs.getOptionString(11),
					rs.getOptionString(12),
					rs.getOptionString(13),
					rs.getOptionString(14),
					rs.getOptionString(15),
					rs.getOptionString(16),
					rs.getOptionString(17),
					rs.getOptionString(18),
					rs.getOptionString(19)
				)

			override def getQuery: String =
				s"""
				   |select
				   |name_first,
				   |name_last,
				   |name_middle_initial,
				   |to_char(dob, 'MM/DD/YYYY'),
				   |email,
				   |addr_1,
				   |addr_2,
				   |addr_3,
				   |city,
				   |state,
				   |zip,
				   |country,
				   |phone_primary,
				   |phone_primary_type,
				   |phone_alternate,
				   |phone_alternate_type,
				   |allergies,
				   |medications,
				   |special_needs
				   |from persons where person_id = ?
        """.stripMargin

			override val params: List[String] = List(juniorId.toString)
		}

		val resultObj = pb.executePreparedQueryForSelect(select).head
		val resultJson: JsValue = Json.toJson(resultObj)
		Ok(resultJson)
	}

	def post() = Action { request =>
		try {
			val parsedRequest = ParsedRequest(request)
			val juniorId: Int = request.body.asJson.map(json => json("personId").toString().toInt).get
			println("required info post: juniorId is " + juniorId)
			val rc: RequestCache = PermissionsAuthority.getRequestCacheMemberWithJuniorId(None, parsedRequest, juniorId)._2.get
			val pb: PersistenceBroker = rc.pb
			val cb: CacheBroker = rc.cb
			val data = request.body.asJson
			data match {
				case None => {
					println("no body")
					new Status(400)("no body")
				}
				case Some(v: JsValue) => {
					val parsed = RequiredInfoShape.apply(v)
					println(parsed)


					val updateQuery = new PreparedQueryForUpdateOrDelete(Set(MemberUserType)) {
						override def getQuery: String =
							s"""
							   |update persons set
							   |name_first=?,
							   |name_last=?,
							   |name_middle_initial=?,
							   |dob=to_date(?,'MM/DD/YYYY'),
							   |email=?,
							   |addr_1=?,
							   |addr_2=?,
							   |addr_3=?,
							   |city=?,
							   |state=?,
							   |zip=?,
							   |country=?,
							   |phone_primary=?,
							   |phone_primary_type=?,
							   |phone_alternate=?,
							   |phone_alternate_type=?,
							   |allergies=?,
							   |medications=?,
							   |special_needs=?
							   |where person_id = ?
              """.stripMargin

						override val params: List[String] = List(
							parsed.firstName.orNull,
							parsed.lastName.orNull,
							parsed.middleInitial.orNull,
							parsed.dob.orNull,
							parsed.childEmail.orNull,
							parsed.addr1.orNull,
							parsed.addr2.orNull,
							parsed.addr3.orNull,
							parsed.city.orNull,
							parsed.state.orNull,
							parsed.zip.orNull,
							parsed.country.orNull,
							parsed.primaryPhone.orNull,
							parsed.primaryPhoneType.orNull,
							parsed.alternatePhone.orNull,
							parsed.alternatePhoneType.orNull,
							parsed.allergies.orNull,
							parsed.medications.orNull,
							parsed.specialNeeds.orNull,
							parsed.personId.toString
						)
					}

					pb.executePreparedQueryForUpdateOrDelete(updateQuery)

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
				Ok("Internal Error")
			}
		}
	}
}


//firstName: string
//middleInitial: string
//lastName: string,
//dobMonth: string,
//dobDate: string,
//dobYear: string,
//childEmail: string,
//addr_1: string,
//addr_2: string,
//addr_3: string,
//city: string,
//state: string,
//zip: string,
//country: string,
//primaryPhoneFirst: string,
//primaryPhoneSecond: string,
//primaryPhoneThird: string,
//primaryPhoneExt: string,
//primaryPhoneType: string,
//alternatePhoneFirst: string,
//alternatePhoneSecond: string,
//alternatePhoneThird: string,
//alternatePhoneExt: string,
//alternatePhoneType: string,
//allergies: string,
//medications: string,
//specialNeeds: string