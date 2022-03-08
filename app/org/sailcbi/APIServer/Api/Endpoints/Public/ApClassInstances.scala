package org.sailcbi.APIServer.Api.Endpoints.Public

import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import com.coleji.neptune.Util.{DateUtil, JsconUtil}
import org.sailcbi.APIServer.Entities.cacheable.ApClassInstances.{ApClassInstanceDto, ApClassInstancesCache, ApClassInstancesCacheKey}
import org.sailcbi.APIServer.UserTypes.PublicRequestCache
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ApClassInstances @Inject()(implicit val exec: ExecutionContext) extends InjectedController {
	def get(startDate: Option[String])(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		PA.withRequestCache(PublicRequestCache)(None, ParsedRequest(req), rc => {
			val (rows, cacheMetadata) = ApClassInstancesCache.get(rc, ApClassInstancesCacheKey(DateUtil.parseWithDefault(startDate)))
			val columnNames = List(
				"INSTANCE_ID",
				"TYPE_NAME",
				"START_DATE",
				"START_TIME",
				"LOCATION_STRING",
				"ENROLLEES"
			)

			Future(Ok(
				JsconUtil.formatJscon(rows.map(mapCaseObjectToJsArray), columnNames, cacheMetadata._1, cacheMetadata._2)
			))
		})
	})

	private def mapCaseObjectToJsArray(caseObject: ApClassInstanceDto): List[JsValue] = List(
		Json.toJson(caseObject.instanceId),
		Json.toJson(caseObject.typeName),
		Json.toJson(caseObject.sessionDate),
		Json.toJson(caseObject.sessionTime),
		Json.toJson(caseObject.location),
		Json.toJson(caseObject.enrollees)
	)
}
