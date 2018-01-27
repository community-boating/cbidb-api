package Api.Endpoints.Public

import java.time.LocalDateTime
import javax.inject.Inject

import Api.Endpoints.Public.JpTeams.JpTeamsParamsObject
import Api.{ParamsObject, PublicRequest}
import Logic.PreparedQueries.Public.{GetJpTeams, GetJpTeamsResult}
import Services.PersistenceBroker
import play.api.libs.json._
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.{ExecutionContext, Future}

class JpTeams @Inject() (implicit exec: ExecutionContext) extends PublicRequest[JpTeamsParamsObject, GetJpTeamsResult] {
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
