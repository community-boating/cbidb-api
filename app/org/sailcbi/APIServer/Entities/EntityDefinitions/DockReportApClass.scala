package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class DockReportApClass extends StorableClass(DockReportApClass) {
	override object values extends ValuesObject {
		val dockReportApClassId = new IntFieldValue(self, DockReportApClass.fields.dockReportApClassId)
		val dockReportId = new IntFieldValue(self, DockReportApClass.fields.dockReportId)
		val apInstanceId = new IntFieldValue(self, DockReportApClass.fields.apInstanceId)
		val className = new StringFieldValue(self, DockReportApClass.fields.className)
		val classDatetime = new DateTimeFieldValue(self, DockReportApClass.fields.classDatetime)
		val location = new NullableStringFieldValue(self, DockReportApClass.fields.location)
		val instructor = new NullableStringFieldValue(self, DockReportApClass.fields.instructor)
		val attend = new NullableIntFieldValue(self, DockReportApClass.fields.attend)
		val createdOn = new NullableDateTimeFieldValue(self, DockReportApClass.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, DockReportApClass.fields.createdBy)
		val updatedOn = new NullableDateTimeFieldValue(self, DockReportApClass.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, DockReportApClass.fields.updatedBy)
	}
}

object DockReportApClass extends StorableObject[DockReportApClass] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "DOCK_REPORT_AP_CLASSES"

	object fields extends FieldsObject {
		val dockReportApClassId = new IntDatabaseField(self, "DOCK_REPORT_AP_CLASS_ID")
		val dockReportId = new IntDatabaseField(self, "DOCK_REPORT_ID")
		@NullableInDatabase
		val apInstanceId = new IntDatabaseField(self, "AP_INSTANCE_ID")
		@NullableInDatabase
		val className = new StringDatabaseField(self, "CLASS_NAME", 100)
		@NullableInDatabase
		val classDatetime = new DateTimeDatabaseField(self, "CLASS_DATETIME")
		val location = new NullableStringDatabaseField(self, "LOCATION", 50)
		val instructor = new NullableStringDatabaseField(self, "INSTRUCTOR", 50)
		val attend = new NullableIntDatabaseField(self, "ATTEND")
		val createdOn = new NullableDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 100)
		val updatedOn = new NullableDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 100)
	}

	def primaryKey: IntDatabaseField = fields.dockReportApClassId
}