package Api.Endpoints

import java.time.LocalDateTime
import javax.inject.Inject

import Api.ApiRequestSync
import CbiUtil.Profiler
import Entities._
import Services.{CacheBroker, PersistenceBroker}
import play.api.inject.ApplicationLifecycle
import play.api.libs.json._
import play.api.mvc.{Action, Controller}

class Users @Inject() (lifecycle: ApplicationLifecycle, cb: CacheBroker, pb: PersistenceBroker) extends Controller {
  def get() = Action {
    val request = UsersRequest()
    val response: String = request.get
    Ok(response).withHeaders(
      CONTENT_TYPE -> "application/json",
      CONTENT_LENGTH -> response.length.toString
    )
  }

  case class UsersRequest() extends ApiRequestSync(cb) {
    def getCacheBrokerKey: String = "users"

    def getExpirationTime: LocalDateTime = {
      LocalDateTime.now.plusMinutes(10)
    }

    object params {}

    def getJSONResult: JsObject = {
      val profiler = new Profiler

      val users: List[User] = pb.getObjectsByFilters(
        User,
        List.empty,
        10
      )


      profiler.lap("did all the databasing")

      val usersArray: JsArray = JsArray(users.map(u => {
        JsArray(IndexedSeq(
          JsNumber(u.userId),
          JsString(u.userName),
          JsString(u.nameFirst),
          JsString(u.nameLast),
          JsBoolean(u.active)
        ))
      }))

      val metaData = JsArray(Seq(
        JsObject(Map("name" -> JsString("USER_ID"))),
        JsObject(Map("name" -> JsString("USER_NAME"))),
        JsObject(Map("name" -> JsString("NAME_FIRST"))),
        JsObject(Map("name" -> JsString("NAME_LAST"))),
        JsObject(Map("name" -> JsString("ACTIVE")))
      ))

      val data = JsObject(Map(
        "rows" -> usersArray,
        "metaData" -> metaData
      ))
      data
    }
  }
}
