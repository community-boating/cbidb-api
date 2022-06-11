package org.sailcbi.APIServer.Api.Endpoints.Staff.Rest.Signouts

import com.coleji.neptune.API.RestController
import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import com.coleji.neptune.Storable.Filter
import com.coleji.neptune.Storable.StorableQuery.QueryBuilder
import com.coleji.neptune.Util.{DateUtil, Profiler}
import org.sailcbi.APIServer.Entities.EntityDefinitions.{Person, PersonRating, Signout, SignoutCrew}
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, InjectedController}

import java.time.{LocalDate, ZonedDateTime}
import javax.inject.Inject
import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

class GetSignouts @Inject()(implicit val exec: ExecutionContext) extends RestController(Signout) with InjectedController {
	val MAX_RECORDS_TO_RETURN = 600

	def getToday()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		PA.withRequestCache(StaffRequestCache)(None, ParsedRequest(req), rc => {
			val p = new Profiler


			// get signouts and skippers, attach skippers to signouts
			val signouts = {
				val qb = QueryBuilder
					.from(Signout)
					.innerJoin(Person, Signout.fields.personId.alias.equalsField(Person.fields.personId.alias))
					.where(Signout.fields.signoutDatetime.alias.isDateConstant(LocalDate.now()))
					.select(List(
						Signout.fields.signoutId.alias,
						Signout.fields.programId.alias,
						Signout.fields.boatId.alias,
						Signout.fields.cardNum.alias,
						Signout.fields.sailNumber.alias,
						Signout.fields.signoutDatetime.alias,
						Signout.fields.signinDatetime.alias,
						Signout.fields.testRatingId.alias,
						Signout.fields.testResult.alias,
						Signout.fields.signoutType.alias,
						Signout.fields.didCapsize.alias,
						Signout.fields.personId.alias,
						Signout.fields.comments.alias,
						Signout.fields.jpAttendanceId.alias,
						Signout.fields.apAttendanceId.alias,
						Signout.fields.hullNumber.alias,

						Person.fields.personId,
						Person.fields.nameFirst,
						Person.fields.nameLast
					))

				rc.executeQueryBuilder(qb).map(qbrr => {
					val skipper = Person.construct(qbrr)
					val signout = Signout.construct(qbrr)
					signout.references.skipper.set(skipper)
					signout
				})
			}

			val signoutIds = signouts.map(_.values.signoutId.get)

			// get crew
			val crew = {
				val crewQb = QueryBuilder
					.from(SignoutCrew)
					.innerJoin(Person, Person.fields.personId.alias.equalsField(SignoutCrew.fields.personId.alias))
					.where(SignoutCrew.fields.signoutId.alias.inList(signoutIds))
					.select(List(
						SignoutCrew.fields.crewId.alias,
						SignoutCrew.fields.cardNum.alias,
						SignoutCrew.fields.signoutId.alias,
						SignoutCrew.fields.personId.alias,
						SignoutCrew.fields.startActive.alias,
						SignoutCrew.fields.endActive.alias,
						SignoutCrew.fields.jpAttendanceId.alias,
						SignoutCrew.fields.apAttendanceId.alias,

						Person.fields.personId,
						Person.fields.nameFirst,
						Person.fields.nameLast
					))

				rc.executeQueryBuilder(crewQb).map(qbrr => {
					val person = Person.construct(qbrr)
					val crew = SignoutCrew.construct(qbrr)
					crew.references.person.set(person)
					crew
				})
			}

			// attach crew to signouts
			signouts.foreach(s => {
				val signoutId = s.values.signoutId.get
				s.references.crew.set(crew.filter(_.values.signoutId.get == signoutId))
			})

			val personIds = signouts
				.map(_.references.skipper.peek.map(_.values.personId.get))
				.filter(_.isDefined)
				.map(_.get)
				.distinct

			// get ratings for skippers
			val personsRatings = {
				val personsRatingsQB = QueryBuilder
					.from(PersonRating)
					.where(PersonRating.fields.personId.alias.inList(personIds))
					.select(List(
						PersonRating.fields.personId.alias,
						PersonRating.fields.ratingId.alias,
						PersonRating.fields.programId.alias,
					))

				rc.executeQueryBuilder(personsRatingsQB).map(PersonRating.construct)
			}

			type PersonsRatingsMap = mutable.HashMap[Int, List[PersonRating]]

			// hash ratings by personid
			val personsRatingsByPerson = personsRatings.foldLeft(new mutable.HashMap[Int, List[PersonRating]])((map: PersonsRatingsMap, pr: PersonRating) => {
				val personId = pr.values.personId.get
				if (!map.contains(personId)) {
					map(personId) = List.empty
				}
				val e = map(personId)
				map(personId) = pr :: e
				map
			})

			// attach ratings to skippers
			signouts.foreach(_.references.skipper.peek.foreach(s => {
				val prs = personsRatingsByPerson.getOrElse(s.values.personId.get, List.empty)
				s.references.personRatings.set(prs)
			}))

			// order by datetime and cap at MAX_RECORDS_TO_RETURN
			val signoutsToReturn = signouts.sortBy(
				_.values.signoutDatetime.get
					.map(_.atZone(DateUtil.HOME_TIME_ZONE).toEpochSecond)
					.getOrElse(LocalDate.now().atStartOfDay(DateUtil.HOME_TIME_ZONE).toEpochSecond)
			).reverse.take(MAX_RECORDS_TO_RETURN)

			if (signouts.size > MAX_RECORDS_TO_RETURN) {
				PA.logger.error("Today's signouts had " + signouts.size + " results, sending an abridged list to client")
			}

			val ret = Future(Ok(Json.toJson(signoutsToReturn)))

			p.lap("completed signouts")

			ret
		})
	})
}
