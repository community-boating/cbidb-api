package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class ApDockRptClasse extends StorableClass(ApDockRptClasse) {
	object values extends ValuesObject {
		val drClassId = new IntFieldValue(self, ApDockRptClasse.fields.drClassId)
		val classId = new NullableIntFieldValue(self, ApDockRptClasse.fields.classId)
		val drId = new NullableIntFieldValue(self, ApDockRptClasse.fields.drId)
		val classTime = new NullableStringFieldValue(self, ApDockRptClasse.fields.classTime)
		val attendance = new NullableDoubleFieldValue(self, ApDockRptClasse.fields.attendance)
		val instructor = new NullableStringFieldValue(self, ApDockRptClasse.fields.instructor)
		val createdOn = new NullableLocalDateTimeFieldValue(self, ApDockRptClasse.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, ApDockRptClasse.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, ApDockRptClasse.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, ApDockRptClasse.fields.updatedBy)
	}
}

object ApDockRptClasse extends StorableObject[ApDockRptClasse] {
	val entityName: String = "AP_DOCK_RPT_CLASSES"

	object fields extends FieldsObject {
		val drClassId = new IntDatabaseField(self, "DR_CLASS_ID")
		val classId = new NullableIntDatabaseField(self, "CLASS_ID")
		val drId = new NullableIntDatabaseField(self, "DR_ID")
		val classTime = new NullableStringDatabaseField(self, "CLASS_TIME", 10)
		val attendance = new NullableDoubleDatabaseField(self, "ATTENDANCE")
		val instructor = new NullableStringDatabaseField(self, "INSTRUCTOR", 100)
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.drClassId
}