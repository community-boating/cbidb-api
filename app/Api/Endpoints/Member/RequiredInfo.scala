package Api.Endpoints.Member

import java.sql.ResultSet
import java.time.LocalDateTime

import Api.Endpoints.Public.JpTeams.JpTeamsParamsObject
import CbiUtil.ParsedRequest
import Entities.EntityDefinitions.User
import IO.PreparedQueries.{HardcodedQueryForSelect, HardcodedQueryForUpdateOrDelete}
import IO.PreparedQueries.Public.GetJpTeams
import Services.Authentication.{PublicUserType, StaffUserType}
import Services.PermissionsAuthority.UnauthorizedAccessException
import Services.{CacheBroker, PermissionsAuthority, PersistenceBroker, RequestCache}
import Storable.ProtoStorable
import javax.inject.Inject
import play.api.libs.json.{JsObject, JsString, JsValue}
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

    case class Result(nameFirst: Option[String], nameLast: Option[String], nameMiddleInitial: Option[String])

    val select = new HardcodedQueryForSelect[Result](Set(PublicUserType)) {
      override def mapResultSetRowToCaseObject(rs: ResultSet): Result =
        Result(rs.getOptionString(1), rs.getOptionString(2), rs.getOptionString(3))

      override def getQuery: String =
        s"""
          |select name_first, name_last, name_middle_initial from persons where person_id = $testJuniorID
        """.stripMargin
    }
    
    val resultObj = pb.executePreparedQueryForSelect(select).head
    val resultJson = JsObject(Map(
      "firstName" -> JsString(resultObj.nameFirst.orNull),
      "lastName" -> JsString(resultObj.nameLast.orNull),
      "middleInitial" -> JsString(resultObj.nameMiddleInitial.orNull)
    ))
    Ok(resultJson)
  }

  def post() = Action { request =>
    try {
      val rc: RequestCache = PermissionsAuthority.getRequestCache(PublicUserType, None, ParsedRequest(request))._2.get
      val pb: PersistenceBroker = rc.pb
      val cb: CacheBroker = rc.cb
      val data = request.body.asFormUrlEncoded
      data match {
        case None => {
          println("no body")
          new Status(400)("no body")
        }
        case Some(v) => {


          val postParams: Map[String, String] = v.map(Function.tupled((s: String, ss: Seq[String]) => (s, ss.mkString(""))))
          println(postParams)

          object values {
            val nameFirst = postParams.getOrElse("nameFirst", null)
            val nameLast = postParams.getOrElse("nameLast", null)
            val nameMiddleInitial = postParams.getOrElse("nameMiddleInitial", null)
          }

          val updateQuery = new HardcodedQueryForUpdateOrDelete(Set(PublicUserType)) {
            override def getQuery: String =
              s"""
                |update persons set
                |name_First='${values.nameFirst}',
                |name_Last='${values.nameLast}',
                |name_middle_initial='${values.nameMiddleInitial}'
                |where person_id = $testJuniorID
              """.stripMargin
          }

          pb.executePreparedQueryForUpdateOrDelete(updateQuery)

          Ok("done")
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