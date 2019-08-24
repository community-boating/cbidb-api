package org.sailcbi.APIServer.Api.Endpoints.Public

import java.time.{LocalDate, LocalDateTime}
import java.time.format.DateTimeFormatter

import org.sailcbi.APIServer.Api.Endpoints.Public.JpClassSections.JpClassSectionsParamsObject
import org.sailcbi.APIServer.Api.{AuthenticatedRequest, CacheableResultFromPreparedQuery, ParamsObject}
import org.sailcbi.APIServer.CbiUtil.DateUtil
import org.sailcbi.APIServer.IO.PreparedQueries.Public.{GetJpClassSections, GetJpClassSectionsResult}
import org.sailcbi.APIServer.Services.Authentication.PublicUserType
import javax.inject.Inject
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.ExecutionContext

class JpClassSections @Inject()(implicit val exec: ExecutionContext)
	extends AuthenticatedRequest with CacheableResultFromPreparedQuery[JpClassSectionsParamsObject, GetJpClassSectionsResult] {
	def get(startDate: Option[String]): Action[AnyContent] = {
		val params = JpClassSectionsParamsObject(DateUtil.parseWithDefault(startDate))
		val pq = new GetJpClassSections(params.startDate)
		evaluate(PublicUserType, params, pq)
	}

	def getCacheBrokerKey(params: JpClassSectionsParamsObject): CacheKey =
		"jp-class-sections-" + params.startDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))

	def getExpirationTime: LocalDateTime = {
		LocalDateTime.now.plusSeconds(5)
	}
}

object JpClassSections {

	case class JpClassSectionsParamsObject(startDate: LocalDate) extends ParamsObject

}