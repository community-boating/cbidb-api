package org.sailcbi.APIServer.Api.Endpoints.Public

import java.time.LocalDateTime

import org.sailcbi.APIServer.Api.Endpoints.Public.JpTeams.JpTeamsParamsObject
import org.sailcbi.APIServer.Api.{AuthenticatedRequest, CacheableResultFromRemoteRequest}
import org.sailcbi.APIServer.CbiUtil.PropertiesWrapper
import org.sailcbi.APIServer.IO.PreparedQueries.Public.GetJpTeamsResult
import org.sailcbi.APIServer.Services.Authentication.PublicUserType
import javax.inject.Inject
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.ExecutionContext


class Weather @Inject()(ws: WSClient)(implicit val exec: ExecutionContext)
		extends AuthenticatedRequest with CacheableResultFromRemoteRequest[JpTeamsParamsObject, GetJpTeamsResult] {
	def get: Action[AnyContent] = {
		evaluate(PublicUserType, new JpTeamsParamsObject, ws, Weather.url)
	}

	def getCacheBrokerKey(params: JpTeamsParamsObject): CacheKey = "weather"

	def getExpirationTime: LocalDateTime = {
		LocalDateTime.now.plusMinutes(10)
	}
}

object Weather {
	val props = new PropertiesWrapper("conf/private/weather-credentials", Array[String]("host", "path"))
	println("Making request to remote weather service")
	val url = "https://" + props.getProperty("host") + props.getProperty("path")
}
