package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Core.UnlockedRequestCache
import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable
import org.sailcbi.APIServer.Entities.EntityDefinitions.PersonRating.CasePersonRating

class Person extends StorableClass(Person) {
	override object references extends ReferencesObject {
		val personRatings = new Initializable[Set[PersonRating]]
	}

	object values extends ValuesObject {
		val personId = new IntFieldValue(self, Person.fields.personId)
		val nameFirst = new NullableStringFieldValue(self, Person.fields.nameFirst)
		val nameMiddleInitial = new NullableStringFieldValue(self, Person.fields.nameMiddleInitial)
		val nameLast = new NullableStringFieldValue(self, Person.fields.nameLast)
		val namePrefix = new NullableStringFieldValue(self, Person.fields.namePrefix)
		val nameSuffix = new NullableStringFieldValue(self, Person.fields.nameSuffix)
		val gender = new NullableStringFieldValue(self, Person.fields.gender)
		val dob = new NullableDateTimeFieldValue(self, Person.fields.dob)
		val email = new NullableStringFieldValue(self, Person.fields.email)
		val parentEmail = new NullableStringFieldValue(self, Person.fields.parentEmail)
		val addr1 = new NullableStringFieldValue(self, Person.fields.addr1)
		val addr2 = new NullableStringFieldValue(self, Person.fields.addr2)
		val addr3 = new NullableStringFieldValue(self, Person.fields.addr3)
		val city = new NullableStringFieldValue(self, Person.fields.city)
		val state = new NullableStringFieldValue(self, Person.fields.state)
		val zip = new NullableStringFieldValue(self, Person.fields.zip)
		val country = new NullableStringFieldValue(self, Person.fields.country)
		val phonePrimary = new NullableStringFieldValue(self, Person.fields.phonePrimary)
		val phonePrimaryType = new NullableStringFieldValue(self, Person.fields.phonePrimaryType)
		val phoneAlternate = new NullableStringFieldValue(self, Person.fields.phoneAlternate)
		val phoneAlternateType = new NullableStringFieldValue(self, Person.fields.phoneAlternateType)
		val emerg1Name = new NullableStringFieldValue(self, Person.fields.emerg1Name)
		val emerg1Relation = new NullableStringFieldValue(self, Person.fields.emerg1Relation)
		val emerg1PhonePrimary = new NullableStringFieldValue(self, Person.fields.emerg1PhonePrimary)
		val emerg1PhoneAlternate = new NullableStringFieldValue(self, Person.fields.emerg1PhoneAlternate)
		val emerg2Name = new NullableStringFieldValue(self, Person.fields.emerg2Name)
		val emerg2Relation = new NullableStringFieldValue(self, Person.fields.emerg2Relation)
		val emerg2PhonePrimary = new NullableStringFieldValue(self, Person.fields.emerg2PhonePrimary)
		val emerg2PhoneAlternate = new NullableStringFieldValue(self, Person.fields.emerg2PhoneAlternate)
		val allergies = new NullableStringFieldValue(self, Person.fields.allergies)
		val medications = new NullableStringFieldValue(self, Person.fields.medications)
		val specialNeeds = new NullableStringFieldValue(self, Person.fields.specialNeeds)
		val badMail = new BooleanFieldValue(self, Person.fields.badMail)
		val deceased = new BooleanFieldValue(self, Person.fields.deceased)
		val doNotCall = new BooleanFieldValue(self, Person.fields.doNotCall)
		val doNotEmail = new BooleanFieldValue(self, Person.fields.doNotEmail)
		val doNotMail = new BooleanFieldValue(self, Person.fields.doNotMail)
		val occupation = new NullableStringFieldValue(self, Person.fields.occupation)
		val employer = new NullableStringFieldValue(self, Person.fields.employer)
		val matchingGifts = new NullableStringFieldValue(self, Person.fields.matchingGifts)
		val citizenship = new NullableStringFieldValue(self, Person.fields.citizenship)
		val ethnicity = new NullableStringFieldValue(self, Person.fields.ethnicity)
		val language = new NullableStringFieldValue(self, Person.fields.language)
		val referralSource = new NullableStringFieldValue(self, Person.fields.referralSource)
		val sailingExp = new NullableStringFieldValue(self, Person.fields.sailingExp)
		val organization = new NullableStringFieldValue(self, Person.fields.organization)
		val orgContact = new NullableStringFieldValue(self, Person.fields.orgContact)
		val student = new NullableBooleanFieldValue(self, Person.fields.student)
		val school = new NullableStringFieldValue(self, Person.fields.school)
//		val createdOn = new NullableLocalDateTimeFieldValue(self, Person.fields.createdOn)
//		val createdBy = new NullableStringFieldValue(self, Person.fields.createdBy)
//		val updatedOn = new NullableLocalDateTimeFieldValue(self, Person.fields.updatedOn)
//		val updatedBy = new NullableStringFieldValue(self, Person.fields.updatedBy)
		val ethnicityOther = new NullableStringFieldValue(self, Person.fields.ethnicityOther)
		val referralOther = new NullableStringFieldValue(self, Person.fields.referralOther)
		val allowSignouts = new NullableStringFieldValue(self, Person.fields.allowSignouts)
		val sendAlert = new BooleanFieldValue(self, Person.fields.sendAlert)
		val temp = new StringFieldValue(self, Person.fields.temp)
		val personType = new NullableIntFieldValue(self, Person.fields.personType)
		val emerg1PhonePrimaryType = new NullableStringFieldValue(self, Person.fields.emerg1PhonePrimaryType)
		val emerg1PhoneAlternateType = new NullableStringFieldValue(self, Person.fields.emerg1PhoneAlternateType)
		val emerg2PhonePrimaryType = new NullableStringFieldValue(self, Person.fields.emerg2PhonePrimaryType)
		val emerg2PhoneAlternateType = new NullableStringFieldValue(self, Person.fields.emerg2PhoneAlternateType)
		val badPhone = new BooleanFieldValue(self, Person.fields.badPhone)
		val badEmail = new BooleanFieldValue(self, Person.fields.badEmail)
		val military = new NullableStringFieldValue(self, Person.fields.military)
		val reducedLunch = new BooleanFieldValue(self, Person.fields.reducedLunch)
		val pwHash = new NullableStringFieldValue(self, Person.fields.pwHash)
		val incomeLevel = new NullableIntFieldValue(self, Person.fields.incomeLevel)
		val swimProof = new NullableIntFieldValue(self, Person.fields.swimProof)
		val oldFundraisingGreeting = new NullableStringFieldValue(self, Person.fields.oldFundraisingGreeting)
		val oldPhoneWork = new NullableStringFieldValue(self, Person.fields.oldPhoneWork)
		val languageOther = new NullableStringFieldValue(self, Person.fields.languageOther)
		val dobVerified = new BooleanFieldValue(self, Person.fields.dobVerified)
		val prevMember = new NullableBooleanFieldValue(self, Person.fields.prevMember)
		val prevCard = new NullableStringFieldValue(self, Person.fields.prevCard)
		val jpAgeOverride = new BooleanFieldValue(self, Person.fields.jpAgeOverride)
		val mergeStatus = new NullableStringFieldValue(self, Person.fields.mergeStatus)
//		val incomeAmt = new NullableDoubleFieldValue(self, Person.fields.incomeAmt)
//		val incomeAmtRaw = new NullableStringFieldValue(self, Person.fields.incomeAmtRaw)
		val specNeedsVerified = new BooleanFieldValue(self, Person.fields.specNeedsVerified)
		val memberComment = new NullableClobFieldValue(self, Person.fields.memberComment)
		val verifiedEmail = new NullableStringFieldValue(self, Person.fields.verifiedEmail)
		val uapBoat = new NullableIntFieldValue(self, Person.fields.uapBoat)
		val ignoreJpMinAge = new BooleanFieldValue(self, Person.fields.ignoreJpMinAge)
		val uapInterestedLessons = new BooleanFieldValue(self, Person.fields.uapInterestedLessons)
		val uapInterestedRides = new BooleanFieldValue(self, Person.fields.uapInterestedRides)
		val entityType = new NullableStringFieldValue(self, Person.fields.entityType)
		val jpTeamId = new NullableIntFieldValue(self, Person.fields.jpTeamId)
		val guestPortalReg = new BooleanFieldValue(self, Person.fields.guestPortalReg)
		val signoutBlockReason = new NullableClobFieldValue(self, Person.fields.signoutBlockReason)
		val dataUnconfirmed = new BooleanFieldValue(self, Person.fields.dataUnconfirmed)
		val campFair = new NullableStringFieldValue(self, Person.fields.campFair)
		val previousMember = new NullableBooleanFieldValue(self, Person.fields.previousMember)
		val protopersonCookie = new NullableStringFieldValue(self, Person.fields.protopersonCookie)
		val protoState = new NullableStringFieldValue(self, Person.fields.protoState)
		val veteranEligible = new BooleanFieldValue(self, Person.fields.veteranEligible)
		val stripeCustomerId = new NullableStringFieldValue(self, Person.fields.stripeCustomerId)
		val hasAuthedAs = new NullableIntFieldValue(self, Person.fields.hasAuthedAs)
		val pwHashScheme = new NullableStringFieldValue(self, Person.fields.pwHashScheme)
		val nextRecurringDonation = new NullableDateTimeFieldValue(self, Person.fields.nextRecurringDonation)
	}

	def setPersonRatings(rc: UnlockedRequestCache): Unit = {
		references.personRatings set rc.getObjectsByFilters(
			PersonRating,
			List(PersonRating.fields.personId.equalsConstant(values.personId.get))
		).toSet
	}

	lazy val casePersonRatings: Set[CasePersonRating] = references.personRatings.get.map(_.asCaseClass)

	// TODO: move to logic
	def hasRatingDirect(ratingId: Int, programId: Int): Boolean = casePersonRatings.contains(CasePersonRating(
		this.values.personId.get,
		ratingId,
		programId
	))

	def hasRatingSomehow(ratings: Set[Rating], ratingId: Int, programId: Int): Boolean = {
		if (hasRatingDirect(ratingId, programId)) true
		else {
			val subRatings: Set[Rating] = ratings.filter(_.values.overriddenBy.get == Some(ratingId))
			subRatings.map(_.values.ratingId.get).foldLeft(false)((agg, r) => agg || hasRatingSomehow(ratings, r, programId))
		}
	}

	override def toString(): String = {
		def showValue(v: FieldValue[_]): String = {
			if (v.getPersistenceLiteral._2.nonEmpty) {
				v.getPersistenceLiteral._2.head
			} else {
				v.getPersistenceLiteral._1
			}
		}
		s"(PERSON ${this.valuesList.filter(_.isSet).map(v => s"[${v.getPersistenceFieldName}:${showValue(v)}]")})"
	}
}

object Person extends StorableObject[Person] {
	val entityName: String = "PERSONS"

	object fields extends FieldsObject {
		val personId = new IntDatabaseField(self, "PERSON_ID")
		val nameFirst = new NullableStringDatabaseField(self, "NAME_FIRST", 100)
		val nameMiddleInitial = new NullableStringDatabaseField(self, "NAME_MIDDLE_INITIAL", 5)
		val nameLast = new NullableStringDatabaseField(self, "NAME_LAST", 100)
		val namePrefix = new NullableStringDatabaseField(self, "NAME_PREFIX", 50)
		val nameSuffix = new NullableStringDatabaseField(self, "NAME_SUFFIX", 50)
		val gender = new NullableStringDatabaseField(self, "GENDER", 1)
		val dob = new NullableDateTimeDatabaseField(self, "DOB")
		val email = new NullableStringDatabaseField(self, "EMAIL", 100)
		val parentEmail = new NullableStringDatabaseField(self, "PARENT_EMAIL", 100)
		val addr1 = new NullableStringDatabaseField(self, "ADDR_1", 200)
		val addr2 = new NullableStringDatabaseField(self, "ADDR_2", 200)
		val addr3 = new NullableStringDatabaseField(self, "ADDR_3", 200)
		val city = new NullableStringDatabaseField(self, "CITY", 50)
		val state = new NullableStringDatabaseField(self, "STATE", 50)
		val zip = new NullableStringDatabaseField(self, "ZIP", 15)
		val country = new NullableStringDatabaseField(self, "COUNTRY", 50)
		val phonePrimary = new NullableStringDatabaseField(self, "PHONE_PRIMARY", 100)
		val phonePrimaryType = new NullableStringDatabaseField(self, "PHONE_PRIMARY_TYPE", 50)
		val phoneAlternate = new NullableStringDatabaseField(self, "PHONE_ALTERNATE", 100)
		val phoneAlternateType = new NullableStringDatabaseField(self, "PHONE_ALTERNATE_TYPE", 50)
		val emerg1Name = new NullableStringDatabaseField(self, "EMERG1_NAME", 200)
		val emerg1Relation = new NullableStringDatabaseField(self, "EMERG1_RELATION", 50)
		val emerg1PhonePrimary = new NullableStringDatabaseField(self, "EMERG1_PHONE_PRIMARY", 100)
		val emerg1PhoneAlternate = new NullableStringDatabaseField(self, "EMERG1_PHONE_ALTERNATE", 100)
		val emerg2Name = new NullableStringDatabaseField(self, "EMERG2_NAME", 200)
		val emerg2Relation = new NullableStringDatabaseField(self, "EMERG2_RELATION", 50)
		val emerg2PhonePrimary = new NullableStringDatabaseField(self, "EMERG2_PHONE_PRIMARY", 100)
		val emerg2PhoneAlternate = new NullableStringDatabaseField(self, "EMERG2_PHONE_ALTERNATE", 100)
		val allergies = new NullableStringDatabaseField(self, "ALLERGIES", 4000)
		val medications = new NullableStringDatabaseField(self, "MEDICATIONS", 4000)
		val specialNeeds = new NullableStringDatabaseField(self, "SPECIAL_NEEDS", 4000)
		val badMail = new BooleanDatabaseField(self, "BAD_MAIL", true)
		val deceased = new BooleanDatabaseField(self, "DECEASED", true)
		val doNotCall = new BooleanDatabaseField(self, "DO_NOT_CALL", true)
		val doNotEmail = new BooleanDatabaseField(self, "DO_NOT_EMAIL", true)
		val doNotMail = new BooleanDatabaseField(self, "DO_NOT_MAIL", true)
		val occupation = new NullableStringDatabaseField(self, "OCCUPATION", 100)
		val employer = new NullableStringDatabaseField(self, "EMPLOYER", 100)
		val matchingGifts = new NullableStringDatabaseField(self, "MATCHING_GIFTS", 1)
		val citizenship = new NullableStringDatabaseField(self, "CITIZENSHIP", 100)
		val ethnicity = new NullableStringDatabaseField(self, "ETHNICITY", 100)
		val language = new NullableStringDatabaseField(self, "LANGUAGE", 100)
		val referralSource = new NullableStringDatabaseField(self, "REFERRAL_SOURCE", 200)
		val sailingExp = new NullableStringDatabaseField(self, "SAILING_EXP", 100)
		val organization = new NullableStringDatabaseField(self, "ORGANIZATION", 100)
		val orgContact = new NullableStringDatabaseField(self, "ORG_CONTACT", 200)
		val student = new NullableBooleanDatabaseField(self, "STUDENT")
		val school = new NullableStringDatabaseField(self, "SCHOOL", 100)
//		val createdOn = new NullableDateTimeDatabaseField(self, "CREATED_ON")
//		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
//		val updatedOn = new NullableDateTimeDatabaseField(self, "UPDATED_ON")
//		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val ethnicityOther = new NullableStringDatabaseField(self, "ETHNICITY_OTHER", 100)
		val referralOther = new NullableStringDatabaseField(self, "REFERRAL_OTHER", 100)
		val allowSignouts = new NullableStringDatabaseField(self, "ALLOW_SIGNOUTS", 1)
		val sendAlert = new BooleanDatabaseField(self, "SEND_ALERT", true)
		val temp = new StringDatabaseField(self, "TEMP", 1)
		val personType = new NullableIntDatabaseField(self, "PERSON_TYPE")
		val emerg1PhonePrimaryType = new NullableStringDatabaseField(self, "EMERG1_PHONE_PRIMARY_TYPE", 50)
		val emerg1PhoneAlternateType = new NullableStringDatabaseField(self, "EMERG1_PHONE_ALTERNATE_TYPE", 50)
		val emerg2PhonePrimaryType = new NullableStringDatabaseField(self, "EMERG2_PHONE_PRIMARY_TYPE", 50)
		val emerg2PhoneAlternateType = new NullableStringDatabaseField(self, "EMERG2_PHONE_ALTERNATE_TYPE", 50)
		val badPhone = new BooleanDatabaseField(self, "BAD_PHONE", true)
		val badEmail = new BooleanDatabaseField(self, "BAD_EMAIL", true)
		val military = new NullableStringDatabaseField(self, "MILITARY", 50)
		val reducedLunch = new BooleanDatabaseField(self, "REDUCED_LUNCH", true)
		val pwHash = new NullableStringDatabaseField(self, "PW_HASH", 100)
		val incomeLevel = new NullableIntDatabaseField(self, "INCOME_LEVEL")
		val swimProof = new NullableIntDatabaseField(self, "SWIM_PROOF")
		val oldFundraisingGreeting = new NullableStringDatabaseField(self, "OLD_FUNDRAISING_GREETING", 300)
		val oldPhoneWork = new NullableStringDatabaseField(self, "OLD_PHONE_WORK", 50)
		val languageOther = new NullableStringDatabaseField(self, "LANGUAGE_OTHER", 200)
		val dobVerified = new BooleanDatabaseField(self, "DOB_VERIFIED", true)
		val prevMember = new NullableBooleanDatabaseField(self, "PREV_MEMBER")
		val prevCard = new NullableStringDatabaseField(self, "PREV_CARD", 50)
		val jpAgeOverride = new BooleanDatabaseField(self, "JP_AGE_OVERRIDE", true)
		val mergeStatus = new NullableStringDatabaseField(self, "MERGE_STATUS", 1)
//		val incomeAmt = new NullableDoubleDatabaseField(self, "INCOME_AMT")
//		val incomeAmtRaw = new NullableStringDatabaseField(self, "INCOME_AMT_RAW", 50)
		val specNeedsVerified = new BooleanDatabaseField(self, "SPEC_NEEDS_VERIFIED", true)
		val memberComment = new NullableClobDatabaseField(self, "MEMBER_COMMENT")
		val verifiedEmail = new NullableStringDatabaseField(self, "VERIFIED_EMAIL", 1000)
		val uapBoat = new NullableIntDatabaseField(self, "UAP_BOAT")
		val ignoreJpMinAge = new BooleanDatabaseField(self, "IGNORE_JP_MIN_AGE", true)
		val uapInterestedLessons = new BooleanDatabaseField(self, "UAP_INTERESTED_LESSONS", true)
		val uapInterestedRides = new BooleanDatabaseField(self, "UAP_INTERESTED_RIDES", true)
		val entityType = new NullableStringDatabaseField(self, "ENTITY_TYPE", 1)
		val jpTeamId = new NullableIntDatabaseField(self, "JP_TEAM_ID")
		val guestPortalReg = new BooleanDatabaseField(self, "GUEST_PORTAL_REG", true)
		val signoutBlockReason = new NullableClobDatabaseField(self, "SIGNOUT_BLOCK_REASON")
		val dataUnconfirmed = new BooleanDatabaseField(self, "DATA_UNCONFIRMED", true)
		val campFair = new NullableStringDatabaseField(self, "CAMP_FAIR", 50)
		val previousMember = new NullableBooleanDatabaseField(self, "PREVIOUS_MEMBER")
		val protopersonCookie = new NullableStringDatabaseField(self, "PROTOPERSON_COOKIE", 50)
		val protoState = new NullableStringDatabaseField(self, "PROTO_STATE", 1)
		val veteranEligible = new BooleanDatabaseField(self, "VETERAN_ELIGIBLE", true)
		val stripeCustomerId = new NullableStringDatabaseField(self, "STRIPE_CUSTOMER_ID", 30)
		val hasAuthedAs = new NullableIntDatabaseField(self, "HAS_AUTHED_AS")
		val pwHashScheme = new NullableStringDatabaseField(self, "PW_HASH_SCHEME", 20)
		val nextRecurringDonation = new NullableDateTimeDatabaseField(self, "NEXT_RECURRING_DONATION")
	}

	def primaryKey: IntDatabaseField = fields.personId
}
