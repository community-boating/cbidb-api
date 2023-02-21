package org.sailcbi.APIServer.Api.Endpoints.Staff.Rest

import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import com.coleji.neptune.Storable.StorableQuery.QueryBuilder
import org.sailcbi.APIServer.Entities.EntityDefinitions.{ApClassFormat, ApClassInstance, ApClassSession, ApClassSignup, ApClassType, ApClassWaitlistResult, Person}
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ApClassSessions @Inject()(implicit val exec: ExecutionContext) extends InjectedController {
	/**
	 * For the dockhouse to pull today's classes
	 */
	def getTodaysSessionsWithSignups()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		PA.withRequestCache(StaffRequestCache)(None, ParsedRequest(req), rc => {
			val qb = QueryBuilder
				.from(ApClassSession)
				.innerJoin(ApClassInstance, ApClassSession.fields.instanceId.alias.equalsField(ApClassInstance.fields.instanceId.alias))
				.where(ApClassSession.fields.sessionDateTime.alias.isDateConstant(rc.PA.now().toLocalDate))
				.select(List(
					ApClassSession.fields.sessionId,
					ApClassSession.fields.instanceId,
					ApClassSession.fields.sessionDateTime,
					ApClassSession.fields.headcount,
					ApClassSession.fields.cancelledDateTime,
					ApClassSession.fields.sessionLength,

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
				))

			val sessions = rc.executeQueryBuilder(qb).map(qbrr => {
				val session = ApClassSession.construct(qbrr)
				val instance = ApClassInstance.construct(qbrr)

				session.references.apClassInstance.set(instance)
				session
			})

			val instanceIds = sessions.map(s => s.values.instanceId.get).distinct

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
				signup.references.apClassWaitlistResult.set(ApClassWaitlistResult.construct(qbrr, ApClassWaitlistResult.aliasOuter))
				signup
			})

			sessions.foreach(_.references.apClassInstance.forEach(
				i => i.references.apClassSignups.set(signups.filter(s => s.values.instanceId.get == i.values.instanceId.get))
			))

			Future(Ok(Json.toJson(sessions)))
		})
	})
}