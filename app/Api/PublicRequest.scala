package Api

import Services.Authentication.PublicUserType
import Services.PermissionsAuthority.UnauthorizedAccessException
import Services.{CacheBroker, RequestCache}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

sealed trait PublicRequest[T <: ParamsObject, U <: ApiDataObject] {
  private val requestClass: AuthenticatedRequest = new AuthenticatedRequest(PublicUserType) {}
  protected val Ok = requestClass.Ok
  protected def getRC(headers: Headers, cookies: Cookies): RequestCache = requestClass.getRC(headers, cookies)
}

abstract class PublicRequestFromPreparedQuery
  [T <: ParamsObject, U <: ApiDataObject]
  (implicit exec: ExecutionContext)
extends CacheableRequestFromPreparedQuery[T, U] with PublicRequest[T, U] {
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
}
