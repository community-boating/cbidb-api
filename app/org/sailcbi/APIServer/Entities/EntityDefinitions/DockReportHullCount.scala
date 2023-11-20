package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class DockReportHullCount extends StorableClass(DockReportHullCount) {
	override object values extends ValuesObject {
		val dockReportHullCtId = new IntFieldValue(self, DockReportHullCount.fields.dockReportHullCtId)
		val dockReportId = new IntFieldValue(self, DockReportHullCount.fields.dockReportId)
		val hullType = new StringFieldValue(self, DockReportHullCount.fields.hullType)
		val inService = new NullableDoubleFieldValue(self, DockReportHullCount.fields.inService)
		val staffTally = new NullableDoubleFieldValue(self, DockReportHullCount.fields.staffTally)
		val createdOn = new NullableDateTimeFieldValue(self, DockReportHullCount.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, DockReportHullCount.fields.createdBy)
		val updatedOn = new NullableDateTimeFieldValue(self, DockReportHullCount.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, DockReportHullCount.fields.updatedBy)
	}
}

object DockReportHullCount extends StorableObject[DockReportHullCount] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "DOCK_REPORT_HULL_COUNTS"

	object fields extends FieldsObject {
		val dockReportHullCtId = new IntDatabaseField(self, "DOCK_REPORT_HULL_CT_ID")
		val dockReportId = new IntDatabaseField(self, "DOCK_REPORT_ID")
		val hullType = new StringDatabaseField(self, "HULL_TYPE", 50)
		val inService = new NullableDoubleDatabaseField(self, "IN_SERVICE")
		val staffTally = new NullableDoubleDatabaseField(self, "STAFF_TALLY")
		val createdOn = new NullableDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 100)
		val updatedOn = new NullableDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 100)
	}

	def primaryKey: IntDatabaseField = fields.dockReportHullCtId
}