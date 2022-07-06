package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._

class DockReportApClass extends StorableClass(DockReportApClass) {
	object values extends ValuesObject {
		val dockReportApClassId = new IntFieldValue(self, DockReportApClass.fields.dockReportApClassId)
		val dockReportId = new IntFieldValue(self, DockReportApClass.fields.dockReportId)
		val apInstanceId = new NullableIntFieldValue(self, DockReportApClass.fields.apInstanceId)
		val className = new StringFieldValue(self, DockReportApClass.fields.className)
		val classDatetime = new DateTimeFieldValue(self, DockReportApClass.fields.classDatetime)
		val location = new NullableStringFieldValue(self, DockReportApClass.fields.location)
		val instructor = new NullableStringFieldValue(self, DockReportApClass.fields.instructor)
		val attend = new NullableIntFieldValue(self, DockReportApClass.fields.attend)
	}
}

object DockReportApClass extends StorableObject[DockReportApClass] {
	override val entityName: String = "DOCK_REPORT_AP_CLASSES"

	object fields extends FieldsObject {
		val dockReportApClassId = new IntDatabaseField(self, "DOCK_REPORT_AP_CLASS_ID")
		val dockReportId = new IntDatabaseField(self, "DOCK_REPORT_ID")
		val apInstanceId = new NullableIntDatabaseField(self, "AP_INSTANCE_ID")
		val className = new StringDatabaseField(self, "CLASS_NAME", 100)
		val classDatetime = new DateTimeDatabaseField(self, "CLASS_DATETIME")
		val location = new NullableStringDatabaseField(self, "LOCATION", 50)
		val instructor = new NullableStringDatabaseField(self, "INSTRUCTOR", 50)
		val attend = new NullableIntDatabaseField(self, "ATTEND")
	}

	def primaryKey: IntDatabaseField = fields.dockReportApClassId
}