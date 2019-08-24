package org.sailcbi.APIServer.Entities.EntityDefinitions

import org.sailcbi.APIServer.CbiUtil.Initializable
import org.sailcbi.APIServer.Entities.EntityDefinitions.PersonRating.CasePersonRating
import org.sailcbi.APIServer.Storable.Fields.FieldValue.IntFieldValue
import org.sailcbi.APIServer.Storable.Fields.IntDatabaseField
import org.sailcbi.APIServer.Storable._

class PersonRating extends StorableClass {
	this.setCompanion(PersonRating)

	object references extends ReferencesObject {
		var person = new Initializable[Person]
		var rating = new Initializable[Rating]
		var program = new Initializable[ProgramType]
	}

	object values extends ValuesObject {
		val assignId = new IntFieldValue(self, PersonRating.fields.assignId)
		val personId = new IntFieldValue(self, PersonRating.fields.personId)
		val ratingId = new IntFieldValue(self, PersonRating.fields.ratingId)
		val programId = new IntFieldValue(self, PersonRating.fields.programId)
	}

	lazy val asCaseClass = CasePersonRating(
		this.values.personId.get,
		this.values.ratingId.get,
		this.values.programId.get
	)
}

object PersonRating extends StorableObject[PersonRating] {
	val entityName: String = "PERSONS_RATINGS"

	object fields extends FieldsObject {
		val assignId = new IntDatabaseField(self, "ASSIGN_ID")
		val personId = new IntDatabaseField(self, "PERSON_ID")
		val ratingId = new IntDatabaseField(self, "RATING_ID")
		val programId = new IntDatabaseField(self, "PROGRAM_ID")
	}

	def primaryKey: IntDatabaseField = fields.assignId

	case class CasePersonRating(personId: Int, ratingId: Int, programId: Int)

}