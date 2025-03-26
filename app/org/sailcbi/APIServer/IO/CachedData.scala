package org.sailcbi.APIServer.IO

import com.coleji.neptune.Core.UnlockedRequestCache
import com.coleji.neptune.Util.DateUtil
import org.sailcbi.APIServer.Entities.EntityDefinitions.{MembershipType, MembershipTypeExp, ProgramType, Rating}
import org.sailcbi.APIServer.Entities.MagicIds

import java.time.LocalDate
import java.time.temporal.ChronoUnit

class CachedData(rc: UnlockedRequestCache) {
	lazy val getJpWeekAlias: (LocalDate => Option[String]) = {
		def isFall(d: LocalDate): Boolean =
			DateUtil.betweenInclusive(d, DateUtil.parse("09/02/2013"), DateUtil.parse("09/29/2013")) ||
				DateUtil.betweenInclusive(d, DateUtil.parse("09/08/2014"), DateUtil.parse("10/13/2014")) ||
				DateUtil.betweenInclusive(d, DateUtil.parse("09/14/2015"), DateUtil.parse("10/24/2015")) ||
				DateUtil.betweenInclusive(d, DateUtil.parse("09/19/2016"), DateUtil.parse("10/30/2016")) ||
				DateUtil.betweenInclusive(d, DateUtil.parse("09/04/2017"), DateUtil.parse("10/29/2017"))

		def isSpring(d: LocalDate): Boolean =
			DateUtil.betweenInclusive(d, DateUtil.parse("04/21/2014"), DateUtil.parse("05/25/2014")) ||
				DateUtil.betweenInclusive(d, DateUtil.parse("04/13/2015"), DateUtil.parse("05/18/2015")) ||
				DateUtil.betweenInclusive(d, DateUtil.parse("04/11/2016"), DateUtil.parse("05/15/2016")) ||
				DateUtil.betweenInclusive(d, DateUtil.parse("04/16/2017"), DateUtil.parse("05/28/2017"))

		def isWeirdWeek(d: LocalDate): Option[String] = {
			if (DateUtil.betweenInclusive(d, DateUtil.parse("08/21/2017"), DateUtil.parse("08/25/2017"))) {
				Some("Week 11")
			} else None
		}

		def checkOffseason(d: LocalDate): Option[String] = {
			if (isFall(d)) Some("Fall")
			else if (isSpring(d)) Some("Spring")
			else isWeirdWeek(d)
		}

		val exps = membershipTypeExps.filter(
			_.references.membershipType.get.getID == MagicIds.MEMBERSHIP_TYPES.JUNIOR_SUMMER
		)

		val yearToStartDate: Map[Int, (LocalDate => Option[String])] = exps.map(exp => (exp.values.season.get, {
			val startDate = exp.values.startDate.get
			(d: LocalDate) => {
				val daysBetween: Long = ChronoUnit.DAYS.between(startDate, d)
				val week: Long = daysBetween / 7
				if (daysBetween >= 0 && week >= 0 && week <= 9) Some("Week " + (week.toInt + 1).toString)
				else checkOffseason(d)
			}
		})).toMap


		(d: LocalDate) =>
			yearToStartDate.get(d.getYear) match {
				case Some(f) => f(d)
				case None => checkOffseason(d)
			}
	}

	lazy val programTypes: List[ProgramType] = rc.getAllObjectsOfClass(ProgramType, Set(
		ProgramType.fields.programId,
		ProgramType.fields.programName,
	))
	lazy val membershipTypes: List[MembershipType] = {
		rc.getAllObjectsOfClass(MembershipType, Set(
			MembershipType.fields.membershipTypeId,
			MembershipType.fields.membershipTypeName,
			MembershipType.fields.programId,
			MembershipType.fields.price,
		)).map(m => {
			m.references.program.findOneInCollection(programTypes, _.values.programId.get == m.values.programId.get)
			m
		})
	}
	lazy val membershipTypeExps: List[MembershipTypeExp] = {
		rc.getAllObjectsOfClass(MembershipTypeExp, Set(
			MembershipTypeExp.fields.expirationId,
			MembershipTypeExp.fields.membershipTypeId,
			MembershipTypeExp.fields.season,
			MembershipTypeExp.fields.startDate,
			MembershipTypeExp.fields.expirationDate,
		)).map(me => {
			me.references.membershipType.findOneInCollection(membershipTypes, _.values.membershipTypeId.get == me.values.membershipTypeId.get)
			me
		})
	}
	lazy val ratings: List[Rating] = rc.getAllObjectsOfClass(Rating, Set(
		Rating.fields.ratingId,
		Rating.fields.ratingName,
		Rating.fields.overriddenBy,
	))
}

