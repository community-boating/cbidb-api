package Entities.EntityDefinitions

import CbiUtil.{Initializable, InitializableFromCollectionSubset}
import Storable.Fields.FieldValue.{IntFieldValue, NullableIntFieldValue}
import Storable.Fields.{IntDatabaseField, NullableIntDatabaseField}
import Storable._

class JpClassInstance extends StorableClass {
	val myself = this
	this.setCompanion(JpClassInstance)

	object references extends ReferencesObject {
		var classLocation = new Initializable[Option[ClassLocation]]
		var classInstructor = new Initializable[Option[ClassInstructor]]
		var jpClassType = new Initializable[JpClassType]
	}

	object values extends ValuesObject {
		val instanceId = new IntFieldValue(self, JpClassInstance.fields.instanceId)
		val instructorId = new NullableIntFieldValue(self, JpClassInstance.fields.instructorId)
		val locationId = new NullableIntFieldValue(self, JpClassInstance.fields.locationId)
		val typeId = new IntFieldValue(self, JpClassInstance.fields.typeId)
	}

	object calculatedValues extends CalculatedValuesObject {
		val sessions = new InitializableFromCollectionSubset[List[JpClassSession], JpClassSession]((s: JpClassSession) => {
			s.values.instanceId.get == myself.values.instanceId.get
		})

		// TODO: is there a way to make this not compile if you call it unsafely?  Better way to structure this?
		lazy val firstSession: JpClassSession = sessions.get.sortWith((a: JpClassSession, b: JpClassSession) => {
			a.values.sessionDateTime.get.isBefore(b.values.sessionDateTime.get)
		}).head
	}

}

object JpClassInstance extends StorableObject[JpClassInstance] {
	val entityName: String = "JP_CLASS_INSTANCES"

	object fields extends FieldsObject {
		val instanceId = new IntDatabaseField(self, "INSTANCE_ID")
		val instructorId = new NullableIntDatabaseField(self, "INSTRUCTOR_ID")
		val locationId = new NullableIntDatabaseField(self, "LOCATION_ID")
		val typeId = new IntDatabaseField(self, "TYPE_ID")
	}

	def primaryKey: IntDatabaseField = fields.instanceId
}
