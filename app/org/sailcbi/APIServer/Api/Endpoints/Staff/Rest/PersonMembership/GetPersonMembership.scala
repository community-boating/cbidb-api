package org.sailcbi.APIServer.Api.Endpoints.Staff.Rest.PersonMembership

import com.coleji.neptune.API.RestController
import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority, RequestCache, UnlockedRequestCache}
import com.coleji.neptune.Storable.StorableQuery.QueryBuilder
import org.sailcbi.APIServer.Entities.EntityDefinitions._
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetPersonMembership @Inject()(implicit val exec: ExecutionContext) extends RestController(PersonMembership) with InjectedController {
	def getAllForPerson(personId: Int)(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		PA.withRequestCache(StaffRequestCache)(None, ParsedRequest(req), rc => {
			Future(Ok(Json.toJson(GetPersonMembership.getAllForPerson(rc, personId))))
		})
	})
}

object GetPersonMembership {
	def getAllForPerson(rc: RequestCache, personId: Int): List[PersonMembership] = {
		val qb = QueryBuilder
			.from(PersonMembership)
			.innerJoin(MembershipType, PersonMembership.fields.membershipTypeId.alias equalsField MembershipType.fields.membershipTypeId.alias)
			.outerJoin(DiscountInstance.aliasOuter, PersonMembership.fields.discountInstanceId.alias equalsField DiscountInstance.fields.instanceId.alias)
			.outerJoin(Discount.aliasOuter, DiscountInstance.fields.discountId.alias equalsField Discount.fields.discountId.alias)
			.outerJoin(GuestPriv.aliasOuter, PersonMembership.fields.assignId.alias equalsField GuestPriv.fields.membershipId.alias)
			.where(PersonMembership.fields.personId.alias.equalsConstant(personId))
			.select(List(
				PersonMembership.fields.assignId,
				PersonMembership.fields.personId,
				PersonMembership.fields.membershipTypeId,
				PersonMembership.fields.voidCloseId,
				PersonMembership.fields.price,
				PersonMembership.fields.purchaseDate,
				PersonMembership.fields.startDate,
				PersonMembership.fields.expirationDate,
				MembershipType.fields.membershipTypeName,
				MembershipType.fields.programId,
				DiscountInstance.fields.instanceId,
				Discount.fields.discountId,
				Discount.fields.discountName,
				GuestPriv.fields.membershipId,
			))

		rc.executeQueryBuilder(qb).map(qbrr => {
			val pm = PersonMembership.construct(qbrr)
			val membershipType = MembershipType.construct(qbrr)
			val discountInstance = DiscountInstance.aliasOuter.construct(qbrr)
			val discount = Discount.aliasOuter.construct(qbrr)
			val gp = GuestPriv.aliasOuter.construct(qbrr)

			discountInstance.foreach(di => discount.foreach(d => di.references.discount.set(d)))

			if (discountInstance.isEmpty) pm.calculations.isDiscountFrozen.set(true)

			pm.references.membershipType.set(membershipType)
			pm.references.discountInstance.set(discountInstance)
			pm.references.guestPriv.set(gp)

			pm
		})
	}
}