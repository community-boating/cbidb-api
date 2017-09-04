package Api.Endpoints

import java.time.LocalDateTime
import javax.inject.Inject

import Api.ApiRequestAsync
import CbiUtil.Profiler
import Entities._
import Services.{CacheBroker, PersistenceBroker}
import play.api.inject.ApplicationLifecycle
import play.api.libs.json._
import play.api.mvc.{Action, Controller}

import scala.concurrent.{ExecutionContext, Future}

class Users @Inject() (lifecycle: ApplicationLifecycle, cb: CacheBroker, pb: PersistenceBroker)(implicit exec: ExecutionContext) extends Controller {
  def get() = Action.async {
    val request = UsersRequest()
    request.getFuture.map(s => {
      Ok(s).as("application/json")
    })
  }

  case class UsersRequest() extends ApiRequestAsync(cb) {
  /*  println("<<<<<<<<<<<<<<<<let's snooze")
    Thread.sleep(2000)
    println("ok im up lets do this>>>>>>>>>>>>>>>")*/
    def getCacheBrokerKey: String = "users"

    def getExpirationTime: LocalDateTime = {
      LocalDateTime.now.plusMinutes(10)
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
