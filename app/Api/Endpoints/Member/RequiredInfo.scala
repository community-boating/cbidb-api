package Api.Endpoints.Member

import java.time.LocalDateTime

import Api.Endpoints.Public.JpTeams.JpTeamsParamsObject
import CbiUtil.ParsedRequest
import Entities.EntityDefinitions.User
import IO.PreparedQueries.Public.GetJpTeams
import Services.Authentication.{PublicUserType, StaffUserType}
import Services.PermissionsAuthority.UnauthorizedAccessException
import Services.{CacheBroker, PermissionsAuthority, PersistenceBroker, RequestCache}
import Storable.ProtoStorable
import javax.inject.Inject
import play.api.mvc.{Action, AnyContent, Controller}

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext

class RequiredInfo @Inject() (implicit exec: ExecutionContext) extends Controller {
  def get: Action[AnyContent] =  Action { request =>
    val rc: RequestCache = PermissionsAuthority.getRequestCache(PublicUserType, None, ParsedRequest(request))._2.get
    val pb: PersistenceBroker = rc.pb
    val cb: CacheBroker = rc.cb
    Ok("boom")
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
          v.foreach(Function.tupled((s: String, ss: Seq[String]) => {
            println("key: "+  s)
            println("value: " + ss.mkString(""))
          }))

          val postParams: Map[String, String] = v.map(Function.tupled((s: String, ss: Seq[String]) => (s, ss.mkString(""))))
          println(postParams)

          Ok("done")
        }
      }
    } catch {
      case _: UnauthorizedAccessException =>  Ok("Access Denied")
      case _: Throwable =>  Ok("Internal Error")
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