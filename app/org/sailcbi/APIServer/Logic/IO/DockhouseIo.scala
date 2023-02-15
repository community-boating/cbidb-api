package org.sailcbi.APIServer.Logic.IO

import com.coleji.neptune.Core.UnlockedRequestCache
import org.sailcbi.APIServer.Entities.EntityDefinitions.{ApClassSignup, FlagChange, Signout, SignoutCrew}

private[Logic] object DockhouseIo {
	def putFlagChange(rc: UnlockedRequestCache, flagColor: String): FlagChange = {
		val fc = new FlagChange
		fc.values.flag.update(flagColor)
		fc.values.changeDatetime.update(rc.PA.now())
		rc.commitObjectToDatabase(fc)
		fc
	}

	def addPersonToApClass(rc: UnlockedRequestCache, personId: Int, instanceId: Int): Either[String, ApClassSignup] = null

	def createSignout(
		rc: UnlockedRequestCache,
		personId: Int,
		programId: Int,
		boatId: Int,
		signoutType: String,
		cardNumber: Option[String],
		sailNumber: Option[String],
		hullNumber: Option[String],
		testRatingId: Option[Int],
		isQueued: Boolean
	): Signout = {
		val s = new Signout
		s.values.personId.update(Some(personId))
		s.values.programId.update(programId)
		s.values.boatId.update(boatId)
		s.values.signoutType.update(signoutType)
		s.values.cardNum.update(cardNumber)
		s.values.sailNumber.update(sailNumber)
		s.values.hullNumber.update(hullNumber)
		s.values.testRatingId.update(testRatingId)
		s.values.isQueued.update(isQueued)
		s.values.signoutDatetime.update(Some(rc.PA.now))

		rc.commitObjectToDatabase(s)

		s
	}

	def createSignoutCrew(rc: UnlockedRequestCache, personId: Int, cardNumber: String, signoutId: Int): SignoutCrew = {
		val c = new SignoutCrew
		c.values.signoutId.update(signoutId)
		c.values.personId.update(Some(personId))
		c.values.cardNum.update(Some(cardNumber))
		c.values.startActive.update(Some(rc.PA.now()))

		rc.commitObjectToDatabase(c)

		c
	}
}
