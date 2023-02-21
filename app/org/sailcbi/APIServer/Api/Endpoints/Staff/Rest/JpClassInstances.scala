package org.sailcbi.APIServer.Api.Endpoints.Staff.Rest

import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import com.coleji.neptune.Util.DateUtil
import org.sailcbi.APIServer.Entities.EntityDefinitions.{JpClassInstance, JpClassSession}
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, InjectedController}

import java.time.ZonedDateTime
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class JpClassInstances @Inject()(implicit val exec: ExecutionContext) extends InjectedController {
	def getThisSeason()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		PA.withRequestCache(StaffRequestCache)(None, ParsedRequest(req), rc => {
			val currentSeason = PA.currentSeason()

			val sessions = rc.getObjectsByFilters(JpClassSession, List(JpClassSession.fields.sessionDateTime.alias.isYearConstant(currentSeason)), Set(
				JpClassSession.fields.sessionId,
				JpClassSession.fields.instanceId,
				JpClassSession.fields.sessionDateTime,
				JpClassSession.fields.lengthOverride,
			), 1200)

			val instanceIds = sessions.map(_.values.instanceId.get).distinct

			val instances = rc.getObjectsByFilters(JpClassInstance, List(JpClassInstance.fields.instanceId.alias.inList(instanceIds)), Set(
				JpClassInstance.fields.instanceId,
				JpClassInstance.fields.typeId,
				JpClassInstance.fields.adminHold,
				JpClassInstance.fields.locationId,
				JpClassInstance.fields.instructorId
			), 1200)

			implicit val ordering: Ordering[ZonedDateTime] = Ordering.by(_.toEpochSecond)

			instances.foreach(i => {
				val instanceId = i.values.instanceId.get
				i.references.jpClassSessions.set(sessions
					.filter(_.values.instanceId.get == instanceId)
					.sortBy(s => DateUtil.toBostonTime(s.values.sessionDateTime.get))
					.toIndexedSeq)
			})

			Future(Ok(Json.toJson(instances)))
		})
	})
}