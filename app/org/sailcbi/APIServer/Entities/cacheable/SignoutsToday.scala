package org.sailcbi.APIServer.Entities.cacheable

import com.coleji.neptune.Core.{CacheableFactory, PermissionsAuthority, RequestCache, UnlockedRequestCache}
import com.coleji.neptune.Storable.StorableQuery.QueryBuilder
import com.coleji.neptune.Util.{DateUtil, Profiler}
import io.sentry.Sentry
import org.sailcbi.APIServer.Entities.EntityDefinitions._
import org.sailcbi.APIServer.Entities.entitycalculations.MaxBoatFlag
import org.sailcbi.APIServer.Logic.RatingLogic

import java.time.{Duration, LocalDate}

object SignoutsToday extends CacheableFactory[Null, IndexedSeq[Signout]]{
	val MAX_RECORDS_TO_RETURN = 600

	override protected val lifetime: Duration = Duration.ofSeconds(3)

	override protected def calculateKey(config: Null): String = CacheKeys.signoutsToday

	override protected def generateResult(rc: RequestCache, config: Null): IndexedSeq[Signout] = rc match {
		case urc: UnlockedRequestCache => getResult(urc)
		case _ => throw new Exception("Locked request cache used for signouts")
	}

	def getResult(rc: UnlockedRequestCache)(implicit PA: PermissionsAuthority): IndexedSeq[Signout] = {
		getObjects(rc)
	}

	def getObjects(rc: UnlockedRequestCache)(implicit PA: PermissionsAuthority): IndexedSeq[Signout] = {
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
					Signout.fields.createdOn.alias,
					Signout.fields.createdBy.alias,
					Signout.fields.updatedOn.alias,
					Signout.fields.updatedBy.alias,

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

		val tests = {
			val qb = QueryBuilder
				.from(SignoutTest)
				.innerJoin(Person, Person.fields.personId.alias.equalsField(SignoutTest.fields.personId.alias))
				.where(SignoutTest.fields.signoutId.alias.inList(signoutIds))
				.select(List(
					SignoutTest.fields.testId,
					SignoutTest.fields.signoutId,
					SignoutTest.fields.personId,
					SignoutTest.fields.ratingId,
					SignoutTest.fields.testResult,
					SignoutTest.fields.instructorString,

					Person.fields.personId,
					Person.fields.nameFirst,
					Person.fields.nameLast
				))

			rc.executeQueryBuilder(qb).map(qbrr => {
				val person = Person.construct(qbrr)
				val test = SignoutTest.construct(qbrr)
				test.references.person.set(person)

				test
			})
		}

		// attach crew to signouts
		signouts.foreach(s => {
			val signoutId = s.values.signoutId.get
			s.references.crew.set(crew.filter(_.values.signoutId.get == signoutId).toIndexedSeq)
			s.references.tests.set(tests.filter(_.values.signoutId.get == signoutId).toIndexedSeq)
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

		// hash ratings by personid
		val personsRatingsByPerson = personIds.map(pid => (pid, personsRatings.filter(_.values.personId.get == pid))).toMap

		val boatTypes = BoatTypes.get(rc, null)
		boatTypes._1.foreach(_.applyFieldMask(Set(BoatType.fields.boatId)))
		val ratings = Ratings.get(rc, null)
		val programs = Programs.get(rc, null)
		programs._1.foreach(_.applyFieldMask(Set(ProgramType.fields.programId)))

		val maxBoatFlagsByPerson = personIds.map(pid => {
			val maxFlags: List[MaxBoatFlag] = RatingLogic.maxFlags(boatTypes._1.toList, programs._1.toList, personsRatingsByPerson(pid), ratings._1.toList)
			(pid, maxFlags)
		}).toMap

		// attach ratings to skippers
		signouts.foreach(_.references.skipper.peek.foreach(s => {
			val prs = personsRatingsByPerson.getOrElse(s.values.personId.get, List.empty)
			s.references.personRatings.set(prs.toIndexedSeq)
			s.calculations.maxBoatFlags.set(maxBoatFlagsByPerson(s.values.personId.get).toIndexedSeq)
		}))

		// order by datetime and cap at MAX_RECORDS_TO_RETURN
		val signoutsToReturn = signouts.sortBy(
			_.values.signoutDatetime.get
				.map(_.atZone(DateUtil.HOME_TIME_ZONE).toEpochSecond)
				.getOrElse(LocalDate.now().atStartOfDay(DateUtil.HOME_TIME_ZONE).toEpochSecond)
		).reverse.take(MAX_RECORDS_TO_RETURN)

		if (signouts.size > MAX_RECORDS_TO_RETURN) {
			Sentry.capture("Today's signouts had " + signouts.size + " results, sending an abridged list to client")
		}

		val ret = signoutsToReturn

		p.lap("completed signouts")

		ret.toIndexedSeq
	}
}
