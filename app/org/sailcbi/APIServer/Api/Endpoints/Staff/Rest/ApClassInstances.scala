package org.sailcbi.APIServer.Api.Endpoints.Staff.Rest

import com.coleji.neptune.API.RestController
import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import com.coleji.neptune.Storable.StorableQuery.QueryBuilder
import org.sailcbi.APIServer.Entities.EntityDefinitions.{ApClassInstance, ApClassSession, ApClassSignup, ApClassWaitlistResult, BoatType, Person}
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ApClassInstances @Inject()(implicit val exec: ExecutionContext) extends RestController(BoatType) with InjectedController {
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

			val signupsQb = QueryBuilder
				.from(ApClassSignup)
				.innerJoin(Person.alias, Person.fields.personId.alias.equalsField(ApClassSignup.fields.personId.alias))
				.outerJoin(ApClassWaitlistResult.aliasOuter, ApClassWaitlistResult.fields.signupId.alias.equalsField(ApClassSignup.fields.signupId.alias))
				.where(ApClassSignup.fields.instanceId.alias.inList(instanceIds))
				.select(List(
					ApClassSignup.fields.signupId.alias,
					ApClassSignup.fields.instanceId.alias,
					ApClassSignup.fields.personId.alias,
					ApClassSignup.fields.orderId.alias,
					ApClassSignup.fields.signupDatetime.alias,
					ApClassSignup.fields.sequence.alias,
					ApClassSignup.fields.signupType.alias,
					ApClassSignup.fields.price.alias,
					ApClassSignup.fields.closeId.alias,
					ApClassSignup.fields.discountInstanceId.alias,
					ApClassSignup.fields.paymentMedium.alias,
					ApClassSignup.fields.ccTransNum.alias,
					ApClassSignup.fields.voidCloseId.alias,
					ApClassSignup.fields.signupNote.alias,
					ApClassSignup.fields.voidedOnline.alias,
					ApClassSignup.fields.paymentLocation.alias,

					ApClassWaitlistResult.fields.signupId.alias(ApClassWaitlistResult.aliasOuter),
					ApClassWaitlistResult.fields.foVmDatetime.alias(ApClassWaitlistResult.aliasOuter),
					ApClassWaitlistResult.fields.wlResult.alias(ApClassWaitlistResult.aliasOuter),
					ApClassWaitlistResult.fields.offerExpDatetime.alias(ApClassWaitlistResult.aliasOuter),
					ApClassWaitlistResult.fields.foAlertDatetime.alias(ApClassWaitlistResult.aliasOuter),

					Person.fields.personId.alias,
					Person.fields.nameFirst.alias,
					Person.fields.nameLast.alias
				))

			val signups = rc.executeQueryBuilder(signupsQb, 2000).map(qbrr => {
				val signup = ApClassSignup.construct(qbrr)
				val person = Person.construct(qbrr)
				signup.references.person.set(person)
				ApClassWaitlistResult.construct(qbrr, ApClassWaitlistResult.aliasOuter) match {
					case Some(wlResult) => signup.references.apClassWaitlistResult.set(wlResult)
					case None =>
				}
				signup
			})

			instances.foreach(i => {
				val instanceId = i.values.instanceId.get
				i.references.apClassSessions.set(sessions.filter(_.values.instanceId.get == instanceId))
				i.references.apClassSignups.set(signups.filter(_.values.instanceId.get == instanceId))
			})

			Future(Ok(Json.toJson(instances)))
		})
	})
}