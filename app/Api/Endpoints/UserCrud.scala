package Api.Endpoints

import javax.inject.Inject

import Entities.EntityDefinitions.User
import Services.PermissionsAuthority.UnauthorizedAccessException
import Services.{CacheBroker, PermissionsAuthority, PersistenceBroker, RequestCache}
import Storable.ProtoStorable
import play.api.mvc.{Action, Controller}

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext

class UserCrud @Inject() (implicit exec: ExecutionContext) extends Controller {
  def post() = Action { request =>
    try {
      val rc: RequestCache = PermissionsAuthority.getRequestCache(request)
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

          val ps: ProtoStorable = ProtoStorable.constructFromStrings(User, postParams)
          println(ps)

          val newUser: User = User.construct(ps, rc, false)
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

          if (userFields.contains("USER_NAME") && v.get("USER_NAME") == Some(ArrayBuffer("JCOLE"))) new Status(400) ("no JCOLE's allowed")
          else {
            println(v.get("USER_NAME"))
            new Status(OK)("wasnt JCOLE so I'm cool w it")
          }
        }
      }
    } catch {
      case _: UnauthorizedAccessException =>  Ok("Access Denied")
      case _: Throwable =>  Ok("Internal Error")
    }
  }
}
