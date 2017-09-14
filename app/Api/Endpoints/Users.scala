package Api.Endpoints

import java.time.LocalDateTime
import javax.inject.Inject

import Api.ApiRequest
import CbiUtil.{JsonUtil, Profiler}
import Entities._
import Services.{CacheBroker, PersistenceBroker}
import play.api.inject.ApplicationLifecycle
import play.api.libs.json._
import play.api.mvc.{Action, Controller}

import scala.concurrent.{ExecutionContext, Future}

class
Users @Inject() (lifecycle: ApplicationLifecycle, cb: CacheBroker, pb: PersistenceBroker)(implicit exec: ExecutionContext) extends Controller {
  def get() = Action.async {
    val request = new UsersRequest()
    request.getFuture.map(s => {
      Ok(s).as("application/json")
    })
  }

  class UsersRequest extends ApiRequest(cb) {
    def getCacheBrokerKey: CacheKey = "users"

    def getExpirationTime: LocalDateTime = {
      LocalDateTime.now.plusSeconds(2)
    }

    object params {}

    def getJSONResultFuture: Future[JsObject] = Future {
      val profiler = new Profiler

      val users: List[User] = pb.getObjectsByFilters(
        User,
        List.empty,
        200
      )


      profiler.lap("did all the databasing")

      val usersArray: JsArray = JsArray(users.map(u => {
        JsArray(IndexedSeq(
          JsNumber(u.userId),
          JsString(u.userName),
          JsString(u.nameFirst),
          JsString(u.nameLast),
          JsBoolean(u.active),
          JsBoolean(u.hideFromClose)
        ))
      }))

      val metaData = JsonUtil.getMetaData(Seq(
        "USER_ID",
        "USER_NAME",
        "NAME_FIRST",
        "NAME_LAST",
        "ACTIVE",
        "HIDE_FROM_CLOSE"
      ))

      val data = JsObject(Map(
        "rows" -> usersArray,
        "metaData" -> metaData
      ))
      data
    }
  }
}
