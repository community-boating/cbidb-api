package Api.Endpoints

import java.time.LocalDateTime
import javax.inject.Inject

import Api.ApiRequest
import CbiUtil.{JsonUtil, Profiler}
import Entities._
import Services.ServerStateWrapper.ss
import Services.{CacheBroker, PermissionsAuthority, PersistenceBroker, RequestCache}
import Storable.Filter
import play.api.libs.json._
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.{ExecutionContext, Future}

class
Users @Inject() (implicit exec: ExecutionContext) extends Controller {

  def get(userID: Option[Int]): Action[AnyContent] = Action.async {request =>
    val rc: RequestCache = PermissionsAuthority.spawnRequestCache(request)
    val pb: PersistenceBroker = rc.pb
    val cb: CacheBroker = rc.cb

    val apiRequest = new UsersRequest(userID)
    apiRequest.getFuture.map(s => {
      Ok(s).as("application/json")
    })
  }

  class UsersRequest(userID: Option[Int]) extends ApiRequest(cb) {
    def getCacheBrokerKey: CacheKey = "users" + (userID match {
      case None => ""
      case Some(id) => id
    })

    def getExpirationTime: LocalDateTime = {
      LocalDateTime.now.plusSeconds(2)
    }

    object params {}

    def getJSONResultFuture: Future[JsObject] = Future {
      val profiler = new Profiler

      val filters: List[Filter] = userID match {
        case None => List.empty
        case Some(id) => List(User.fields.userId.equalsConstant(id))
      }

      val users: List[User] = pb.getObjectsByFilters(
        User,
        filters,
        200
      )

      profiler.lap("did all the databasing")

      val usersArray: JsArray = JsArray(users.map(u => {
        JsArray(IndexedSeq(
          JsNumber(u.values.userId.get),
          JsString(u.values.userName.get),
          JsString(u.values.nameFirst.get),
          JsString(u.values.nameLast.get),
          JsString(u.values.email.get),
          JsBoolean(u.values.active.get),
          JsBoolean(u.values.hideFromClose.get)
        ))
      }))

      val metaData = JsonUtil.getMetaData(Seq(
        "USER_ID",
        "USER_NAME",
        "NAME_FIRST",
        "NAME_LAST",
        "EMAIL",
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
