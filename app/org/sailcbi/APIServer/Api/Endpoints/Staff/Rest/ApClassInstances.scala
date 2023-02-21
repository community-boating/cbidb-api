package org.sailcbi.APIServer.Api.Endpoints.Staff.Rest

import com.coleji.neptune.API.RestController
import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import com.coleji.neptune.Storable.StorableQuery.QueryBuilder
import com.coleji.neptune.Util.DateUtil
import org.sailcbi.APIServer.Entities.EntityDefinitions._
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, InjectedController}

import java.time.ZonedDateTime
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ApClassInstances @Inject()(implicit val exec: ExecutionContext) extends InjectedController {
	def getThisSeason()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		PA.withRequestCache(StaffRequestCache)(None, ParsedRequest(req), rc => {
			val currentSeason = PA.currentSeason()

			val sessions = rc.getObjectsByFilters(ApClassSession, List(ApClassSession.fields.sessionDateTime.alias.isYearConstant(currentSeason)), Set(
				ApClassSession.fields.sessionId,
				ApClassSession.fields.instanceId,
				ApClassSession.fields.sessionDateTime,
				ApClassSession.fields.headcount,
				ApClassSession.fields.cancelledDateTime,
				ApClassSession.fields.sessionLength,
			), 1200)

			val instanceIds = sessions.map(_.values.instanceId.get).distinct

			val instances = rc.getObjectsByFilters(ApClassInstance, List(ApClassInstance.fields.instanceId.alias.inList(instanceIds)), Set(
				ApClassInstance.fields.instanceId,
				ApClassInstance.fields.signupsStartOverride,
				ApClassInstance.fields.signupMin,
				ApClassInstance.fields.signupMax,
				ApClassInstance.fields.formatId,
				ApClassInstance.fields.cancelByOverride,
				ApClassInstance.fields.price,
				ApClassInstance.fields.cancelledDatetime,
				ApClassInstance.fields.hideOnline,
				ApClassInstance.fields.locationString,
			), 1200)

			implicit val ordering: Ordering[ZonedDateTime] = Ordering.by(_.toEpochSecond)

			instances.foreach(i => {
				val instanceId = i.values.instanceId.get
				i.references.apClassSessions.set(sessions
					.filter(_.values.instanceId.get == instanceId)
					.sortBy(s => DateUtil.toBostonTime(s.values.sessionDateTime.get))
				)
			})

			Future(Ok(Json.toJson(instances)))
		})
	})
}