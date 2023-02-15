package org.sailcbi.APIServer.Api.Endpoints.Staff.Rest

import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import com.coleji.neptune.Storable.StorableQuery.QueryBuilder
import org.sailcbi.APIServer.Entities.EntityDefinitions.{ApClassFormat, ApClassInstance, ApClassSession, ApClassType}
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
				.innerJoin(ApClassFormat, ApClassInstance.fields.formatId.alias.equalsField(ApClassFormat.fields.formatId))
				.innerJoin(ApClassType, ApClassFormat.fields.typeId.alias.equalsField(ApClassType.fields.typeId))
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

					ApClassFormat.fields.formatId,
					ApClassFormat.fields.typeId,
					ApClassFormat.fields.signupMinDefaultOverride,
					ApClassFormat.fields.signupMaxDefaultOverride,
					ApClassFormat.fields.sessionCtDefault,
					ApClassFormat.fields.sessionLengthDefault,
					ApClassFormat.fields.priceDefaultOverride,

					ApClassType.fields.typeId,
					ApClassType.fields.typeName,
					ApClassType.fields.displayOrder,
					ApClassType.fields.noSignup,
					ApClassType.fields.priceDefault,
					ApClassType.fields.signupMinDefault,
					ApClassType.fields.signupMaxDefault,
					ApClassType.fields.disallowIfOverkill,
				))

			val sessions = rc.executeQueryBuilder(qb).map(qbrr => {
				val session = ApClassSession.construct(qbrr)
				val instance = ApClassInstance.construct(qbrr)
				val format = ApClassFormat.construct(qbrr)
				val classType = ApClassType.construct(qbrr)

				format.references.apClassType.set(classType)
				instance.references.apClassFormat.set(format)
				session.references.apClassInstance.set(instance)
				session
			})

			Future(Ok(Json.toJson(sessions)))
		})
	})
}