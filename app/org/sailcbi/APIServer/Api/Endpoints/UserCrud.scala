package org.sailcbi.APIServer.Api.Endpoints

import com.coleji.framework.Core.{CacheBroker, PermissionsAuthority}
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.Entities.EntityDefinitions.User
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.mvc.InjectedController

import javax.inject.Inject
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.{ExecutionContext, Future}

class UserCrud @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority) = Action.async { request =>
		PA.withRequestCache(StaffRequestCache)(None, ParsedRequest(request), rc => {

			val cb: CacheBroker = rc.cb
			val data = request.body.asFormUrlEncoded
			data match {
				case None => {
					println("no body")
					Future(new Status(400)("no body"))
				}
				case Some(v) => {
					v.foreach(Function.tupled((s: String, ss: Seq[String]) => {
						println("key: " + s)
						println("value: " + ss.mkString(""))
					}))

					val postParams: Map[String, String] = v.map(Function.tupled((s: String, ss: Seq[String]) => (s, ss.mkString(""))))

//					val ps: ProtoStorable[String] = ProtoStorable.constructFromStrings(User, postParams)
//					println(ps)
//
//					val newUser: User = User.construct(ps, rc)
//					println("committing!")
//					pb.commitObjectToDatabase(newUser)

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

					if (userFields.contains("USER_NAME") && v.get("USER_NAME") == Some(ArrayBuffer("JCOLE"))) Future(new Status(400)("no JCOLE's allowed"))
					else {
						println(v.get("USER_NAME"))
						Future(new Status(OK)("wasnt JCOLE so I'm cool w it"))
					}
				}
			}
		})
	}
}
