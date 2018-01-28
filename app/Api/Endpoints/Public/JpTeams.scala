package Api.Endpoints.Public

import java.time.LocalDateTime
import javax.inject.Inject

import Api.Endpoints.Public.JpTeams.JpTeamsParamsObject
import Api.{ParamsObject, PublicRequestFromPreparedQuery}
import Logic.PreparedQueries.Public.{GetJpTeams, GetJpTeamsResult}
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.ExecutionContext

class JpTeams @Inject() (implicit exec: ExecutionContext) extends PublicRequestFromPreparedQuery[JpTeamsParamsObject, GetJpTeamsResult] {
  def get: Action[AnyContent] = {
    evaluate(new JpTeamsParamsObject, new GetJpTeams)
  }

  def getCacheBrokerKey(params: JpTeamsParamsObject): CacheKey = "jp-teams"

  def getExpirationTime: LocalDateTime = {
    LocalDateTime.now.plusSeconds(5)
  }
}

object JpTeams {
  class JpTeamsParamsObject extends ParamsObject
}
