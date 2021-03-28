package org.sailcbi.APIServer.Api.Endpoints.Public

import com.coleji.framework.API.{CacheableResultFromPreparedQuery, ParamsObject}
import org.sailcbi.APIServer.Api.Endpoints.Public.JpClassSections.JpClassSectionsParamsObject
import org.sailcbi.APIServer.CbiUtil.DateUtil
import org.sailcbi.APIServer.IO.PreparedQueries.Public.{GetJpClassSections, GetJpClassSectionsResult}
import org.sailcbi.APIServer.UserTypes.PublicRequestCache
import org.sailcbi.APIServer.Services.PermissionsAuthority
import play.api.mvc.{Action, AnyContent}

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime}
import javax.inject.Inject
import scala.concurrent.ExecutionContext

class JpClassSections @Inject()(implicit val exec: ExecutionContext)
	extends CacheableResultFromPreparedQuery[JpClassSectionsParamsObject, GetJpClassSectionsResult] {
	def get(startDate: Option[String])(implicit PA: PermissionsAuthority): Action[AnyContent] = {
		val params = JpClassSectionsParamsObject(DateUtil.parseWithDefault(startDate))
		val pq = new GetJpClassSections(params.startDate)
		evaluate(PublicRequestCache, params, pq)
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
