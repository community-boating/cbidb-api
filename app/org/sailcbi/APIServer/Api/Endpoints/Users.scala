package org.sailcbi.APIServer.Api.Endpoints

import java.time.LocalDateTime

import javax.inject.Inject
import org.sailcbi.APIServer.Api.Endpoints.Users.UsersParamsObject
import org.sailcbi.APIServer.Api.{CacheableResultFromPreparedQuery, ParamsObject}
import org.sailcbi.APIServer.IO.PreparedQueries.Staff.{GetUsers, GetUsersResult}
import org.sailcbi.APIServer.Services.Authentication.StaffUserType
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.ExecutionContext

class Users @Inject()(implicit val exec: ExecutionContext)
		extends CacheableResultFromPreparedQuery[UsersParamsObject, GetUsersResult] {
	def get(): Action[AnyContent] = {
		val params = new UsersParamsObject()
		val pq = new GetUsers
		evaluate(StaffUserType, params, pq)
	}

	def getCacheBrokerKey(params: UsersParamsObject): CacheKey =
		"users"

	def getExpirationTime: LocalDateTime = {
		LocalDateTime.now.plusSeconds(5)
	}
}

object Users {

	class UsersParamsObject extends ParamsObject

}

/*
  // TODO: remove this test harness, put the trycatch back!
  def get(userID: Option[Int]): Action[AnyContent] = Action.async {request =>
  //  try {
      val rc: RequestCache = PermissionsAuthority.getRequestCache(request.headers, request.cookies)
      val pb: PersistenceBroker = rc.pb
      val cb: CacheBroker = rc.cb

  /*    var startDate: LocalDate = LocalDate.now.minusYears(7)
      while(startDate.toEpochDay < LocalDate.now.toEpochDay) {
        println(startDate.toString + "  -  " + rc.logic.dateLogic.getJpWeekAlias(startDate))
        startDate = startDate.plusDays(1)
      }*/

      val apiRequest = new UsersRequest(pb, cb, userID)
      apiRequest.getFuture.map(s => {
        Ok(s).as("application/json")
      })
   /* } catch {
      case _: UnauthorizedAccessException => Future{ Ok("Access Denied") }
      case _: Throwable => Future{ Ok("Internal Error") }
    }*/
  }

  class UsersRequest(pb: PersistenceBroker, cb: CacheBroker, userID: Option[Int]) extends CacheableRequest {
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
  }*/
