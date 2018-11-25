package Api.Endpoints.Security

import Services.Authentication.{MemberUserType, StaffUserType, UserType}
import Services.PermissionsAuthority
import javax.inject.Inject
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.{ExecutionContext, Future}

class GetPwHashForUser @Inject()(implicit exec: ExecutionContext) extends Controller {
  def get(userName: String, userType: String = "staff"): Action[AnyContent] = Action.async {request => Future {
    println("userType is " + userType)
    println("username is " + userName)
    val userTypeObj: Option[UserType] = userType match {
      case "staff" => {
        println("looking up staff")
        Some(StaffUserType)
      }
      case "member" => {
        println("looking up member")
        Some(MemberUserType)
      }
      case _ => None
    }
    println("headers: " + request.headers)
    if (userTypeObj.isEmpty) Ok("BAD USER TYPE")
    else {
      try {
        PermissionsAuthority.getPwHashForUser(request, userName, userTypeObj.get) match {
          case None => Ok("NO DATA")
          // Int is the hashing scheme ID, string is the hash itself
          case Some(t: (Int, String)) => Ok(t._1 + "," + t._2)
        }
      } catch {
        case e: Exception => {
          println(e)
          Ok("NO DATA")
        }
      }

    }
  }}
}
