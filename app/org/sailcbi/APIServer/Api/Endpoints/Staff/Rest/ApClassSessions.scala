package org.sailcbi.APIServer.Api.Endpoints.Staff.Rest

import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority, UnlockedRequestCache}
import com.coleji.neptune.Storable.StorableQuery.QueryBuilder
import org.sailcbi.APIServer.Api.Endpoints.Dto.Staff.Rest.ApClassSessions.Today.{StaffRestApClassSessionsTodayGetResponseSuccessDto, StaffRestApClassSessionsTodayGetResponseSuccessDto_ApClassInstance, StaffRestApClassSessionsTodayGetResponseSuccessDto_ApClassInstance_ApClassSignups, StaffRestApClassSessionsTodayGetResponseSuccessDto_ApClassInstance_ApClassSignups_ApClassWaitlistResult, StaffRestApClassSessionsTodayGetResponseSuccessDto_ApClassInstance_ApClassSignups_Person}
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
			Future(Ok(Json.toJson(mapToDto(getTodaysSessionsWithSignupsImpl(rc)))))
		})
	})

	private def getTodaysSessionsWithSignupsImpl(rc: UnlockedRequestCache): List[ApClassSession] = {
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
		sessions
	}

	private def mapToDto(result: List[ApClassSession]): List[StaffRestApClassSessionsTodayGetResponseSuccessDto] = {
		result.map(session => new StaffRestApClassSessionsTodayGetResponseSuccessDto(
			sessionId = session.values.sessionId.get,
			instanceId = session.values.instanceId.get,
			headcount = session.values.headcount.get,
			cancelledDatetime = session.values.cancelledDateTime.get.map(_.toString),
			sessionDatetime = session.values.sessionDateTime.get.toString,
			sessionLength = session.values.sessionLength.get,
			$$apClassInstance = {
				val instance = session.references.apClassInstance.get
				new StaffRestApClassSessionsTodayGetResponseSuccessDto_ApClassInstance(
					instanceId = instance.values.instanceId.get,
					cancelledDatetime = instance.values.cancelledDatetime.get.map(_.toString),
					signupsStartOverride = instance.values.signupsStartOverride.get.map(_.toString),
					signupMin = instance.values.signupMin.get,
					price = instance.values.price.get,
					signupMax = instance.values.signupMax.get,
					formatId = instance.values.formatId.get,
					hideOnline = instance.values.hideOnline.get,
					cancelByOverride = instance.values.cancelByOverride.get.map(_.toString),
					locationString = instance.values.locationString.get,
					$$apClassSignups = instance.references.apClassSignups.get.map(s =>
						new StaffRestApClassSessionsTodayGetResponseSuccessDto_ApClassInstance_ApClassSignups(
							instanceId = s.values.instanceId.get,
							discountInstanceId = s.values.discountInstanceId.get,
							voidedOnline = s.values.voidedOnline.get,
							personId = s.values.personId.get,
							orderId = s.values.orderId.get,
							price = s.values.price.get,
							signupId = s.values.signupId.get,
							closeId = s.values.closeId.get,
							sequence = s.values.sequence.get,
							paymentMedium = s.values.paymentMedium.get,
							ccTransNum = s.values.ccTransNum.get,
							paymentLocation = s.values.paymentLocation.get,
							voidCloseId = s.values.voidCloseId.get,
							signupType = s.values.signupType.get,
							signupNote = s.values.signupNote.get,
							signupDatetime = s.values.signupDatetime.get.toString,
							$$person = {
								val person = s.references.person.get
								new StaffRestApClassSessionsTodayGetResponseSuccessDto_ApClassInstance_ApClassSignups_Person(
									personId = person.values.personId.get,
									nameFirst = person.values.nameFirst.get,
									nameLast = person.values.nameLast.get
								)
							},
							$$apClassWaitlistResult = {
								s.references.apClassWaitlistResult.get.map(wlr =>
									new StaffRestApClassSessionsTodayGetResponseSuccessDto_ApClassInstance_ApClassSignups_ApClassWaitlistResult(
										wlResult = wlr.values.wlResult.get,
										foVmDatetime = wlr.values.foVmDatetime.get.map(_.toString),
										offerExpDatetime = wlr.values.offerExpDatetime.get.map(_.toString),
										signupId = wlr.values.signupId.get,
										foAlertDatetime = wlr.values.foAlertDatetime.get.map(_.toString)
									)
								)
							}
						)
					)
				)
			}
		))
	}
}