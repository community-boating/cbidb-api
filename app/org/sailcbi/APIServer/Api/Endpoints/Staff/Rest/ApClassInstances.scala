package org.sailcbi.APIServer.Api.Endpoints.Staff.Rest

import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import org.sailcbi.APIServer.Api.Endpoints.Dto.Staff.Rest.ApClassInstances.ThisSeason.{StaffRestApClassInstancesThisSeasonGetResponseSuccessDto, StaffRestApClassInstancesThisSeasonGetResponseSuccessDto_ApClassSessions, StaffRestApClassInstancesThisSeasonGetResponseSuccessDto_Instructor}
import org.sailcbi.APIServer.Entities.EntityDefinitions._
import org.sailcbi.APIServer.Entities.cacheable.ApClassInstancesThisSeason
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ApClassInstances @Inject()(implicit val exec: ExecutionContext) extends InjectedController {
	def getThisSeason()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		PA.withRequestCache(StaffRequestCache)(None, ParsedRequest(req), rc => {
			Future(Ok(Json.toJson(toDto(ApClassInstancesThisSeason.get(rc, null)._1.toList))))
		})
	})

	def toDto(instances: List[ApClassInstance]): List[StaffRestApClassInstancesThisSeasonGetResponseSuccessDto] = {
		instances.map(i => new StaffRestApClassInstancesThisSeasonGetResponseSuccessDto(
			instanceId = i.values.instanceId.get,
			cancelledDatetime = i.values.cancelledDatetime.get.map(_.toString),
			signupsStartOverride = i.values.signupsStartOverride.get.map(_.toString),
			signupMin = i.values.signupMin.get,
			price = i.values.price.get,
			signupMax = i.values.signupMax.get,
			formatId = i.values.formatId.get,
			hideOnline = i.values.hideOnline.get,
			cancelByOverride = i.values.cancelByOverride.get.map(_.toString),
			locationString = i.values.locationString.get,
			doNotAutoCancel = i.values.doNotAutoCancel.get,
			instructorId = i.values.instructorId.get,
			$$instructor = i.references.instructor.peek.flatMap(_.map(ii => new StaffRestApClassInstancesThisSeasonGetResponseSuccessDto_Instructor(
				personId = ii.values.personId.get,
				nameFirst = ii.values.nameFirst.get,
				nameLast = ii.values.nameLast.get
			))),
			$$apClassSessions = i.references.apClassSessions.peek.map(ss => ss.map(s => new StaffRestApClassInstancesThisSeasonGetResponseSuccessDto_ApClassSessions(
				sessionId = s.values.sessionId.get,
				instanceId = s.values.instanceId.get,
				headcount = s.values.headcount.get,
				cancelledDatetime = s.values.cancelledDatetime.get.map(_.toString),
				sessionDatetime = s.values.sessionDatetime.get.toString,
				sessionLength = s.values.sessionLength.get,
				isMakeup = s.values.isMakeup.get
			))).getOrElse(List.empty).toList
		))
	}
}