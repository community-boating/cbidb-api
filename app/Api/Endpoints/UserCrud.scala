package Api.Endpoints

import javax.inject.Inject

import Entities.User
import Services.{CacheBroker, PersistenceBroker}
import play.api.inject.ApplicationLifecycle
import play.api.mvc.{Action, Controller}

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext

class UserCrud @Inject() (lifecycle: ApplicationLifecycle, cb: CacheBroker, pb: PersistenceBroker)(implicit exec: ExecutionContext) extends Controller {
  def post() = Action { request => {
    val data = request.body.asFormUrlEncoded
    data match {
      case None => {
        println("no body")
        new Status(400)("no body")
      }
      case Some(v) => {
        val userFields: Set[String] = User.fieldList.map(_.getFieldName).toSet
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
  }}
}