package org.sailcbi.APIServer.Api.Endpoints.Staff.Rest.PersonRest

import com.coleji.neptune.API.RestController
import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import com.coleji.neptune.Storable.StorableQuery.QueryBuilder
import org.sailcbi.APIServer.Entities.EntityDefinitions.{Person, PersonCard}
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.{JsNull, Json}
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetPerson @Inject()(implicit val exec: ExecutionContext) extends RestController(Person) with InjectedController {
	def getOneForSummaryScreen(personId: Int)(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		PA.withRequestCache(StaffRequestCache)(None, ParsedRequest(req), rc => {
			val person = getOne(rc, personId, Set(
				Person.fields.personId,
				Person.fields.nameFirst,
				Person.fields.nameLast,
				Person.fields.dob,
				Person.fields.email,
				Person.fields.phonePrimary,
				Person.fields.phoneAlternate,
				Person.fields.emerg1Name,
				Person.fields.emerg1Relation,
				Person.fields.emerg1PhonePrimary,
				Person.fields.emerg1PhoneAlternate,
				Person.fields.emerg2Name,
				Person.fields.emerg2Relation,
				Person.fields.emerg2PhonePrimary,
				Person.fields.emerg2PhoneAlternate,
			))
			Future(Ok(Json.toJson(person)))
		})
	})

	def findByCardNumber(cardNumber: String)(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		PA.withRequestCache(StaffRequestCache)(None, ParsedRequest(req), rc => {
			val qb = QueryBuilder
				.from(Person)
				.innerJoin(PersonCard, PersonCard.fields.personId.alias.equalsField(Person.fields.personId.alias))
				.where(List(
					PersonCard.fields.cardNum.alias.equalsConstant(cardNumber),
					PersonCard.fields.active.alias.equalsConstant(true)
				))
				.select(List(
					Person.fields.personId.alias,
					Person.fields.nameFirst.alias,
					Person.fields.nameLast.alias
				))

			val op = rc.executeQueryBuilder(qb).map(Person.construct).headOption
			Future(Ok(op.map(p => Json.toJson(p)).getOrElse(JsNull)))
		})
	})
}