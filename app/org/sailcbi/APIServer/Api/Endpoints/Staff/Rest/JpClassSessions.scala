package org.sailcbi.APIServer.Api.Endpoints.Staff.Rest

import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import com.coleji.neptune.Storable.StorableQuery.QueryBuilder
import org.sailcbi.APIServer.Entities.EntityDefinitions.{JpClassInstance, JpClassSession, JpClassSignup, JpClassWlResult, Person}
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class JpClassSessions @Inject()(implicit val exec: ExecutionContext) extends InjectedController {
	/**
	 * For the dockhouse to pull today's classes
	 */
	def getTodaysSessionsWithSignups()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		PA.withRequestCache(StaffRequestCache)(None, ParsedRequest(req), rc => {
			val qb = QueryBuilder
				.from(JpClassSession)
				.innerJoin(JpClassInstance, JpClassSession.fields.instanceId.alias.equalsField(JpClassInstance.fields.instanceId.alias))
				.where(JpClassSession.fields.sessionDateTime.alias.isDateConstant(rc.PA.now().toLocalDate))
				.select(List(
					JpClassSession.fields.sessionId,
					JpClassSession.fields.instanceId,
					JpClassSession.fields.sessionDateTime,
					JpClassSession.fields.lengthOverride,

					JpClassInstance.fields.instanceId,
					JpClassInstance.fields.typeId,
					JpClassInstance.fields.adminHold,
					JpClassInstance.fields.locationId,
					JpClassInstance.fields.instructorId
				))

			val sessions = rc.executeQueryBuilder(qb).map(qbrr => {
				val session = JpClassSession.construct(qbrr)
				val instance = JpClassInstance.construct(qbrr)

				session.references.jpClassInstance.set(instance)
				session
			})

			val instanceIds = sessions.map(s => s.values.instanceId.get).distinct

			val signupsQb = QueryBuilder
				.from(JpClassSignup)
				.innerJoin(Person.alias, Person.fields.personId.alias.equalsField(JpClassSignup.fields.personId.alias))
				.outerJoin(JpClassWlResult.aliasOuter, JpClassWlResult.fields.signupId.alias.equalsField(JpClassSignup.fields.signupId.alias))
				.where(JpClassSignup.fields.instanceId.alias.inList(instanceIds))
				.select(List(
					JpClassSignup.fields.signupId.alias,
					JpClassSignup.fields.instanceId.alias,
					JpClassSignup.fields.personId.alias,
					JpClassSignup.fields.signupDatetime.alias,
					JpClassSignup.fields.sequence.alias,
					JpClassSignup.fields.signupType.alias,
					JpClassSignup.fields.sectionId.alias,
					JpClassSignup.fields.groupId.alias,

					JpClassWlResult.fields.signupId.alias(JpClassWlResult.aliasOuter),
					JpClassWlResult.fields.wlResult.alias(JpClassWlResult.aliasOuter),
					JpClassWlResult.fields.offerExpDatetime.alias(JpClassWlResult.aliasOuter),

					Person.fields.personId.alias,
					Person.fields.nameFirst.alias,
					Person.fields.nameLast.alias
				))

			val signups = rc.executeQueryBuilder(signupsQb, 2000).map(qbrr => {
				val signup = JpClassSignup.construct(qbrr)
				val person = Person.construct(qbrr)
				signup.references.person.set(person)
				signup.references.jpClassWlResult.set(JpClassWlResult.construct(qbrr, JpClassWlResult.aliasOuter))
				signup
			})

			sessions.foreach(_.references.jpClassInstance.forEach(
				i => i.references.jpClassSignups.set(signups.filter(s => s.values.instanceId.get == i.values.instanceId.get).toIndexedSeq)
			))

			Future(Ok(Json.toJson(sessions)))
		})
	})
}