package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.framework.Core.UnlockedRequestCache
import com.coleji.framework.Storable.FieldValues.{FieldValue, IntFieldValue, NullableStringFieldValue}
import com.coleji.framework.Storable.Fields.{IntDatabaseField, NullableStringDatabaseField}
import com.coleji.framework.Storable._
import com.coleji.framework.Util.Initializable
import org.sailcbi.APIServer.Entities.EntityDefinitions.PersonRating.CasePersonRating

class Person extends StorableClass {
	val instance: Person = this
	this.setCompanion(Person)

	object references extends ReferencesObject {
		val personRatings = new Initializable[Set[PersonRating]]
	}

	object values extends ValuesObject {
		val personId = new IntFieldValue(self, Person.fields.personId)
		val nameFirst = new NullableStringFieldValue(self, Person.fields.nameFirst)
		val nameLast = new NullableStringFieldValue(self, Person.fields.nameLast)
		val email = new NullableStringFieldValue(self, Person.fields.email)
		val pwHash = new NullableStringFieldValue(self, Person.fields.pwHash)
		val addr1 = new NullableStringFieldValue(self, Person.fields.addr1)
		val addr2 = new NullableStringFieldValue(self, Person.fields.addr2)
		val addr3 = new NullableStringFieldValue(self, Person.fields.addr3)
		val city = new NullableStringFieldValue(self, Person.fields.city)
		val state = new NullableStringFieldValue(self, Person.fields.state)
		val zip = new NullableStringFieldValue(self, Person.fields.zip)
	}

	override val valuesList = List(
		values.personId,
		values.nameFirst,
		values.nameLast,
		values.email,
		values.pwHash,
		values.addr1,
		values.addr2,
		values.addr3,
		values.city,
		values.state,
		values.zip
	)

	def setPersonRatings(rc: UnlockedRequestCache): Unit = {
		references.personRatings set rc.getObjectsByFilters(
			PersonRating,
			List(PersonRating.fields.personId.equalsConstant(instance.values.personId.get))
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
		val nameLast = new NullableStringDatabaseField(self, "NAME_LAST", 100)
		val email = new NullableStringDatabaseField(self, "EMAIL", 100)
		val pwHash = new NullableStringDatabaseField(self, "PW_HASH", 100)
		val addr1 = new NullableStringDatabaseField(self, "ADDR_1", 100)
		val addr2 = new NullableStringDatabaseField(self, "ADDR_2", 100)
		val addr3 = new NullableStringDatabaseField(self, "ADDR_3", 100)
		val city = new NullableStringDatabaseField(self, "CITY", 100)
		val state = new NullableStringDatabaseField(self, "STATE", 50)
		val zip = new NullableStringDatabaseField(self, "ZIP", 15)
	}

	def primaryKey: IntDatabaseField = fields.personId
}
