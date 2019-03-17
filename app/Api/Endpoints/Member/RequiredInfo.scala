package Api.Endpoints.Member

import java.sql.ResultSet
import java.time.LocalDateTime

import Api.Endpoints.Public.JpTeams.JpTeamsParamsObject
import CbiUtil.ParsedRequest
import Entities.EntityDefinitions.User
import IO.PreparedQueries.{HardcodedQueryForSelect, HardcodedQueryForUpdateOrDelete, PreparedQueryForUpdateOrDelete}
import IO.PreparedQueries.Public.GetJpTeams
import Services.Authentication.{PublicUserType, StaffUserType}
import Services.PermissionsAuthority.UnauthorizedAccessException
import Services.{CacheBroker, PermissionsAuthority, PersistenceBroker, RequestCache}
import Storable.ProtoStorable
import javax.inject.Inject
import play.api.libs.json.{JsObject, JsString, JsValue, Json}
import play.api.mvc.{Action, AnyContent, Controller}

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext

class RequiredInfo @Inject() (implicit exec: ExecutionContext) extends Controller {
  val testJuniorID = 188911

  def get: Action[AnyContent] =  Action { request =>
    Thread.sleep(4000)
    val rc: RequestCache = PermissionsAuthority.getRequestCache(PublicUserType, None, ParsedRequest(request))._2.get
    val pb: PersistenceBroker = rc.pb
    val cb: CacheBroker = rc.cb

    val select = new HardcodedQueryForSelect[RequiredInfoShape](Set(PublicUserType)) {
      override def mapResultSetRowToCaseObject(rs: ResultSet): RequiredInfoShape =
        RequiredInfoShape(rs.getOptionString(1), rs.getOptionString(2), rs.getOptionString(3))

      override def getQuery: String =
        s"""
          |select name_first, name_last, name_middle_initial from persons where person_id = $testJuniorID
        """.stripMargin
    }
    
    val resultObj = pb.executePreparedQueryForSelect(select).head
    val resultJson: JsValue = Json.toJson(resultObj)
    Ok(resultJson)
  }

  def post() = Action { request =>
    try {
      val rc: RequestCache = PermissionsAuthority.getRequestCache(PublicUserType, None, ParsedRequest(request))._2.get
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

          // TODO: make prepared
          val updateQuery = new PreparedQueryForUpdateOrDelete(Set(PublicUserType)) {
            override def getQuery: String =
              s"""
                |update persons set
                |name_First=?,
                |name_Last=?,
                |name_middle_initial=?
                |where person_id = ?
              """.stripMargin

            override val params: List[String] = List(
              parsed.firstName.orNull,
              parsed.lastName.orNull,
              parsed.middleInitial.orNull,
              testJuniorID.toString
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
      case _: UnauthorizedAccessException =>  Ok("Access Denied")
      case e: Throwable =>  {
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