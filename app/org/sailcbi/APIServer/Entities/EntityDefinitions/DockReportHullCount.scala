package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._

class DockReportHullCount extends StorableClass(DockReportHullCount) {
	object values extends ValuesObject {
		val dockReportHullCtId = new IntFieldValue(self, DockReportHullCount.fields.dockReportHullCtId)
		val dockReportId = new IntFieldValue(self, DockReportHullCount.fields.dockReportId)
		val hullType = new StringFieldValue(self, DockReportHullCount.fields.hullType)
		val inService = new NullableIntFieldValue(self, DockReportHullCount.fields.inService)
		val staffTally = new NullableIntFieldValue(self, DockReportHullCount.fields.staffTally)
	}
}

object DockReportHullCount extends StorableObject[DockReportHullCount] {
	val entityName: String = "DOCK_REPORT_HULL_COUNTS"

	object fields extends FieldsObject {
		val dockReportHullCtId = new IntDatabaseField(self, "DOCK_REPORT_HULL_CT_ID")
		val dockReportId = new IntDatabaseField(self, "DOCK_REPORT_ID")
		val hullType = new StringDatabaseField(self, "HULL_TYPE", 50)
		val inService = new NullableIntDatabaseField(self, "IN_SERVICE")
		val staffTally = new NullableIntDatabaseField(self, "STAFF_TALLY")
	}

	def primaryKey: IntDatabaseField = fields.dockReportHullCtId
}