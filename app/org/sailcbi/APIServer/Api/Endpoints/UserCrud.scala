package org.sailcbi.APIServer.Api.Endpoints

import javax.inject.Inject
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.Entities.EntityDefinitions.User
import org.sailcbi.APIServer.Services.Authentication.StaffUserType
import org.sailcbi.APIServer.Services.Exception.UnauthorizedAccessException
import org.sailcbi.APIServer.Services.{CacheBroker, PermissionsAuthority, PersistenceBroker, RequestCache}
import org.sailcbi.APIServer.Storable.ProtoStorable
import play.api.mvc.{Action, Controller}

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext

class UserCrud @Inject()(implicit exec: ExecutionContext, PA: PermissionsAuthority) extends Controller {
	def post() = Action { request =>
		try {
			val rc: RequestCache = PA.getRequestCache(StaffUserType, None, ParsedRequest(request)).get
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
						println("key: " + s)
						println("value: " + ss.mkString(""))
					}))

					val postParams: Map[String, String] = v.map(Function.tupled((s: String, ss: Seq[String]) => (s, ss.mkString(""))))

					val ps: ProtoStorable[String] = ProtoStorable.constructFromStrings(User, postParams)
					println(ps)

					val newUser: User = User.construct(ps, rc)
					println("committing!")
					pb.commitObjectToDatabase(newUser)

					val userFields: Set[String] = User.fieldList.map(_.getPersistenceFieldName).toSet
					val reqFields: Set[String] = v.keySet
					val unspecifiedFields: Set[String] = userFields -- reqFields
					println(userFields)
					println(reqFields)
					println(unspecifiedFields)
					if (unspecifiedFields.size == 1 && unspecifiedFields.contains("USER_ID")) {
						println("Good to create")
					} else if (unspecifiedFields.isEmpty) {
						println("Good to update")
					} else {
						println("Missing fields: " + unspecifiedFields)
					}

					if (userFields.contains("USER_NAME") && v.get("USER_NAME") == Some(ArrayBuffer("JCOLE"))) new Status(400)("no JCOLE's allowed")
					else {
						println(v.get("USER_NAME"))
						new Status(OK)("wasnt JCOLE so I'm cool w it")
					}
				}
			}
		} catch {
			case _: UnauthorizedAccessException => Ok("Access Denied")
			case _: Throwable => Ok("Internal Error")
		}
	}
}
