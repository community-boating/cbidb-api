package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.framework.Storable.FieldValues.IntFieldValue
import com.coleji.framework.Storable.Fields.IntDatabaseField
import com.coleji.framework.Storable._
import com.coleji.framework.Util.Initializable
import org.sailcbi.APIServer.Entities.EntityDefinitions.PersonRating.CasePersonRating

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

	override val valuesList = List(
		values.assignId,
		values.personId,
		values.ratingId,
		values.programId
	)

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