package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues.IntFieldValue
import com.coleji.neptune.Storable.Fields.IntDatabaseField
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable
import org.sailcbi.APIServer.Entities.EntityDefinitions.PersonRating.CasePersonRating

class PersonRating extends StorableClass(PersonRating) {
	override object references extends ReferencesObject {
		val person = new Initializable[Person]
		val rating = new Initializable[Rating]
		val program = new Initializable[ProgramType]
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
	override val useRuntimeFieldnamesForJson: Boolean = true

	object fields extends FieldsObject {
		val assignId = new IntDatabaseField(self, "ASSIGN_ID")
		val personId = new IntDatabaseField(self, "PERSON_ID")
		val ratingId = new IntDatabaseField(self, "RATING_ID")
		val programId = new IntDatabaseField(self, "PROGRAM_ID")
	}

	def primaryKey: IntDatabaseField = fields.assignId

	case class CasePersonRating(personId: Int, ratingId: Int, programId: Int)

}