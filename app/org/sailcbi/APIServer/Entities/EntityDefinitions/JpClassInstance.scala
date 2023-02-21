package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues.{BooleanFieldValue, IntFieldValue, NullableIntFieldValue}
import com.coleji.neptune.Storable.Fields.{BooleanDatabaseField, IntDatabaseField, NullableIntDatabaseField}
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.{Initializable, InitializableFromCollectionSubset}

class JpClassInstance extends StorableClass(JpClassInstance) {
	override object references extends ReferencesObject {
		val classLocation = new Initializable[Option[ClassLocation]]
		val classInstructor = new Initializable[Option[ClassInstructor]]
		val jpClassType = new Initializable[JpClassType]
		val jpClassSessions = new Initializable[IndexedSeq[JpClassSession]]
		val jpClassSignups = new Initializable[IndexedSeq[JpClassSignup]]
	}

	object values extends ValuesObject {
		val instanceId = new IntFieldValue(self, JpClassInstance.fields.instanceId)
		val instructorId = new NullableIntFieldValue(self, JpClassInstance.fields.instructorId)
		val locationId = new NullableIntFieldValue(self, JpClassInstance.fields.locationId)
		val typeId = new IntFieldValue(self, JpClassInstance.fields.typeId)
		val adminHold = new BooleanFieldValue(self, JpClassInstance.fields.adminHold)
	}

	object calculatedValues extends CalculatedValuesObject {
		val sessions = new InitializableFromCollectionSubset[List[JpClassSession], JpClassSession]((s: JpClassSession) => {
			s.values.instanceId.get == values.instanceId.get
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
		val adminHold =  new BooleanDatabaseField(self, "ADMIN_HOLD", true)
	}

	def primaryKey: IntDatabaseField = fields.instanceId
}
