package Api.Endpoints.Public

import java.time.LocalDateTime
import javax.inject.Inject

import Api.Endpoints.Public.JpTeams.JpTeamsParamsObject
import Api.{AuthenticatedRequest, CacheableResultFromPreparedQuery, ParamsObject}
import IO.PreparedQueries.Public.{GetJpTeams, GetJpTeamsResult}
import Services.Authentication.PublicUserType
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.ExecutionContext

class JpTeams @Inject() (implicit val exec: ExecutionContext)
extends AuthenticatedRequest with CacheableResultFromPreparedQuery[JpTeamsParamsObject, GetJpTeamsResult] {
  def get: Action[AnyContent] = {
    evaluate(PublicUserType, new JpTeamsParamsObject, new GetJpTeams)
  }

  def getCacheBrokerKey(params: JpTeamsParamsObject): CacheKey = "jp-teams"

  def getExpirationTime: LocalDateTime = {
    LocalDateTime.now.plusSeconds(5)
  }
}

object JpTeams {
  class JpTeamsParamsObject extends ParamsObject
}
