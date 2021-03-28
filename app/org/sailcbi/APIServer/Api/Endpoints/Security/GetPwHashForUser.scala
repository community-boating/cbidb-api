package org.sailcbi.APIServer.Api.Endpoints.Security

import com.coleji.framework.Core.{PermissionsAuthority, RequestCacheObject}
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.UserTypes.{MemberRequestCache, StaffRequestCache}
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetPwHashForUser @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def get(userName: String, userType: String = "staff")(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		Future {
			println("userType is " + userType)
			println("username is " + userName)
			val userTypeObj: Option[RequestCacheObject[_]] = userType match {
				case "staff" => {
					println("looking up staff")
					Some(StaffRequestCache)
				}
				case "member" => {
					println("looking up member")
					Some(MemberRequestCache)
				}
				case _ => None
			}
			println("headers: " + request.headers)
			if (userTypeObj.isEmpty) Ok("BAD USER TYPE")
			else {
				try {
					PA.getPwHashForUser(ParsedRequest(request), userName, userTypeObj.get) match {
						case None => Ok("NO DATA")
						// Int is the hashing scheme ID, string is the hash itself
						case Some(t: (String, String, String)) => Ok(t._1 + "," + t._2 + "," + t._3)
					}
				} catch {
					case e: Exception => {
						println(e)
						Ok("NO DATA")
					}
				}

			}
		}
	}
}
