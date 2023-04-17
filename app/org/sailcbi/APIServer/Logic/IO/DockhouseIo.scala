package org.sailcbi.APIServer.Logic.IO

import com.coleji.neptune.Core.UnlockedRequestCache
import org.sailcbi.APIServer.Entities.EntityDefinitions._
import org.sailcbi.APIServer.Entities.MagicIds

private[Logic] object DockhouseIo {
	def putFlagChange(rc: UnlockedRequestCache, flagColor: String): FlagChange = {
		val fc = new FlagChange
		fc.values.flag.update(flagColor)
		fc.values.changeDatetime.update(rc.PA.now())
		rc.commitObjectToDatabase(fc)
		fc
	}

	def addPersonToApClass(rc: UnlockedRequestCache, personId: Int, instanceId: Int): Either[String, ApClassSignup] = {
		val existingSignup = rc.getObjectsByFilters(ApClassSignup, List(
			ApClassSignup.fields.personId.alias.equalsConstant(personId),
			ApClassSignup.fields.instanceId.alias.equalsConstant(instanceId)
		), Set(
			ApClassSignup.fields.signupId,
			ApClassSignup.fields.signupType,
		)).headOption

		existingSignup match {
			case Some(s) => {
				if (!s.values.signupType.get.equals(MagicIds.CLASS_SIGNUP_TYPES.ENROLLED)) {
					s.values.signupType.update(MagicIds.CLASS_SIGNUP_TYPES.ENROLLED)
					rc.commitObjectToDatabase(s)
				}
				Right(s)
			}
			case None => {
				val otherSignups = rc.getObjectsByFilters(ApClassSignup, List(
					ApClassSignup.fields.instanceId.alias.equalsConstant(instanceId)
				), Set(
					ApClassSignup.fields.signupId,
					ApClassSignup.fields.sequence
				))

				val maxSequence = otherSignups.foldRight(0)((signup, max) => {
					val seq = signup.values.sequence.get
					if (seq > max) seq
					else max
				})

				val s = new ApClassSignup
				s.values.instanceId.update(instanceId)
				s.values.personId.update(personId)
				s.values.signupType.update(MagicIds.CLASS_SIGNUP_TYPES.ENROLLED)
				s.values.signupDatetime.update(rc.PA.now())
				s.values.sequence.update(maxSequence+1)
				rc.commitObjectToDatabase(s)
				Right(s)
			}
		}
	}

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

		testRatingId match {
			case Some(id) => {
				val st = new SignoutTest
				st.values.signoutId.update(s.values.signoutId.get)
				st.values.personId.update(personId)
				st.values.ratingId.update(id)

				rc.commitObjectToDatabase(st)

				s.references.tests.set(IndexedSeq(st))
			}
			case None =>
		}

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
