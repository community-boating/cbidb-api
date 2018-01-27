package Api

import Logic.PreparedQueries.PreparedQueryCaseResult
import Services.Authentication.PublicUserType
import Services.PermissionsAuthority.UnauthorizedAccessException
import Services.{CacheBroker, PersistenceBroker, RequestCache}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

abstract class PublicRequest[T <: ParamsObject, U <: PreparedQueryCaseResult](implicit exec: ExecutionContext) extends CacheableRequest[T, U] {
  protected def evaluate(params: T, pq: PQ): Action[AnyContent] = Action.async {request =>
    try {
      val rc = getRC(request.headers, request.cookies)
      val cb: CacheBroker = rc.cb
      val pb = rc.pb
      getFuture(cb, pb, params, pq).map(s => {
        Ok(s).as("application/json")
      })
    } catch {
      case _: UnauthorizedAccessException => Future{ Ok("Access Denied") }
      case _: Throwable => Future{ Ok("Internal Error") }
    }
  }

  private val requestClass: AuthenticatedRequest = new AuthenticatedRequest(PublicUserType) {}
  private val Ok = requestClass.Ok
  private def getRC(headers: Headers, cookies: Cookies): RequestCache = requestClass.getRC(headers, cookies)
}
