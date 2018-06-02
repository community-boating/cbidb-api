package Api.Endpoints.Public

import java.time.LocalDateTime

import Api.Endpoints.Public.FlagColor.FlagColorParamsObject
import Api.{AuthenticatedRequest, CacheableResultFromPreparedQuery, ParamsObject}
import CbiUtil.ParsedRequest
import IO.PreparedQueries.Public.{GetFlagColor, GetFlagColorResult}
import Services.Authentication.PublicUserType
import Services.CacheBroker
import Services.PermissionsAuthority.UnauthorizedAccessException
import javax.inject.Inject
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.{ExecutionContext, Future}

class FlagColor @Inject() (implicit val exec: ExecutionContext)
  extends AuthenticatedRequest with CacheableResultFromPreparedQuery[FlagColorParamsObject, GetFlagColorResult] {
  def get: Action[AnyContent] = Action.async{request =>
    try {
      val rc = getRC(PublicUserType, ParsedRequest(request))
      val cb: CacheBroker = rc.cb
      val pb = rc.pb
      getFuture(cb, pb, new FlagColorParamsObject, new GetFlagColor).map(s => {
        val r = ".*\\[\\[\"([A-Z])\"\\]\\].*".r
        val f = s.toString match {
          case r(flag: String) => flag
          case _ => "C"
        }

        Ok("var FLAG_COLOR = \"" + f + "\"")
      })
    } catch {
      case _: UnauthorizedAccessException => Future{ Ok("Access Denied") }
      case _: Throwable => Future{ Ok("Internal Error") }
    }
  }

  def getCacheBrokerKey(params: FlagColorParamsObject): CacheKey = "flag-color"

  def getExpirationTime: LocalDateTime = {
    LocalDateTime.now.plusSeconds(5)
  }
}

object FlagColor {
  class FlagColorParamsObject extends ParamsObject
}
