package org.sailcbi.APIServer.Api.Endpoints.Staff

import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import org.sailcbi.APIServer.Entities.EntityDefinitions.JpClassInstance
import org.sailcbi.APIServer.IO.JP.{AllJPClassInstances, AllJpClassSignups, GetJPClassStaggers, GetWeeks}
import org.sailcbi.APIServer.IO.PreparedQueries.Member.{GetClassInstancesQuery, GetClassInstancesQueryResult}
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, InjectedController}

import java.time.LocalDateTime
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetJPClassSignups @Inject()(implicit val exec: ExecutionContext) extends InjectedController {
	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		PA.withRequestCache(StaffRequestCache)(None, ParsedRequest(req), rc => {
			val weeks = GetWeeks.getWeeks(rc)
			val instancesWithSpotsLeft = rc.executePreparedQueryForSelect(GetClassInstancesQuery.public)
			val instances = AllJPClassInstances.get(rc).map(Function.tupled((instance, start, end, sessionLength) => JpClassInstanceDecorated(
				jpClassInstance = instance,
				firstSession = start,
				lastSession = end,
				week = findWeek(weeks, start),
				spotsLeftHTML = findInstance(instancesWithSpotsLeft, instance.values.instanceId.get).map(_.spotsLeft).orNull,
				sessionLength = sessionLength,
			))).filter(_.spotsLeftHTML != null)

			val instanceIds = instances.map(t => t.jpClassInstance.values.instanceId.get)

			val signups = AllJpClassSignups.get(rc, instanceIds)
			val staggers = GetJPClassStaggers.get(rc, instanceIds)



			implicit val writes = Json.writes[JpClassInstanceDecorated]
			val result = Json.toJson(Map(
				"instances" -> Json.toJson(instances),
				"signups" -> Json.toJson(signups),
				"weeks" -> Json.toJson(weeks),
				"staggers" -> Json.toJson(staggers),
			))
			Future(Ok(Json.toJson(result)))
		})
	})

	private def findWeek(weeks: Array[GetWeeks.GetWeeksResult], startDate: LocalDateTime): Int = {
		weeks.find(w =>
			(w.monday.isEqual(startDate.toLocalDate) || w.monday.isBefore(startDate.toLocalDate)) // monday is before the class
			&& w.monday.plusDays(6).isAfter(startDate.toLocalDate)						// next monday is after the class
		).map(_.weekNumber).getOrElse(-1)
	}

	private def findInstance(instances: List[GetClassInstancesQueryResult], instanceId: Int): Option[GetClassInstancesQueryResult] = {
		instances.find(_.instanceId == instanceId)
	}

	case class JpClassInstanceDecorated(
		jpClassInstance: JpClassInstance,
		firstSession: LocalDateTime,
		lastSession: LocalDateTime,
		week: Int,
		spotsLeftHTML: String,
		sessionLength: Double
	)
}
