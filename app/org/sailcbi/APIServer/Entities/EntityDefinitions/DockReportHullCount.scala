package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class DockReportHullCount extends StorableClass(DockReportHullCount) {
	object values extends ValuesObject {
		val dockReportHullCtId = new IntFieldValue(self, DockReportHullCount.fields.dockReportHullCtId)
		val dockReportId = new IntFieldValue(self, DockReportHullCount.fields.dockReportId)
		val hullType = new NullableStringFieldValue(self, DockReportHullCount.fields.hullType)
		val inService = new NullableDoubleFieldValue(self, DockReportHullCount.fields.inService)
		val staffTally = new NullableDoubleFieldValue(self, DockReportHullCount.fields.staffTally)
	}
}

object DockReportHullCount extends StorableObject[DockReportHullCount] {
	val entityName: String = "DOCK_REPORT_HULL_COUNTS"

	object fields extends FieldsObject {
		val dockReportHullCtId = new IntDatabaseField(self, "DOCK_REPORT_HULL_CT_ID")
		val dockReportId = new IntDatabaseField(self, "DOCK_REPORT_ID")
		val hullType = new NullableStringDatabaseField(self, "HULL_TYPE", 50)
		val inService = new NullableDoubleDatabaseField(self, "IN_SERVICE")
		val staffTally = new NullableDoubleDatabaseField(self, "STAFF_TALLY")
	}

	def primaryKey: IntDatabaseField = fields.dockReportHullCtId
}