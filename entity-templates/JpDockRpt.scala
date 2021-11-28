package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class JpDockRpt extends StorableClass(JpDockRpt) {
	object values extends ValuesObject {
		val drId = new IntFieldValue(self, JpDockRpt.fields.drId)
		val drDate = new NullableLocalDateTimeFieldValue(self, JpDockRpt.fields.drDate)
		val mercSo = new NullableDoubleFieldValue(self, JpDockRpt.fields.mercSo)
		val kmSo = new NullableDoubleFieldValue(self, JpDockRpt.fields.kmSo)
		val laserSo = new NullableDoubleFieldValue(self, JpDockRpt.fields.laserSo)
		val 420So = new NullableDoubleFieldValue(self, JpDockRpt.fields.420So)
		val r19So = new NullableDoubleFieldValue(self, JpDockRpt.fields.r19So)
		val sonarSo = new NullableDoubleFieldValue(self, JpDockRpt.fields.sonarSo)
		val wsSo = new NullableDoubleFieldValue(self, JpDockRpt.fields.wsSo)
		val kayakSo = new NullableDoubleFieldValue(self, JpDockRpt.fields.kayakSo)
		val launchesAvail = new NullableStringFieldValue(self, JpDockRpt.fields.launchesAvail)
		val capsizes = new NullableDoubleFieldValue(self, JpDockRpt.fields.capsizes)
		val maxWait = new NullableDoubleFieldValue(self, JpDockRpt.fields.maxWait)
		val ra = new NullableDoubleFieldValue(self, JpDockRpt.fields.ra)
		val totalCrew = new NullableDoubleFieldValue(self, JpDockRpt.fields.totalCrew)
		val damage = new NullableStringFieldValue(self, JpDockRpt.fields.damage)
		val injuries = new NullableStringFieldValue(self, JpDockRpt.fields.injuries)
		val excursions = new NullableStringFieldValue(self, JpDockRpt.fields.excursions)
		val activities = new NullableStringFieldValue(self, JpDockRpt.fields.activities)
		val notes = new NullableStringFieldValue(self, JpDockRpt.fields.notes)
		val supSo = new NullableDoubleFieldValue(self, JpDockRpt.fields.supSo)
		val createdOn = new NullableLocalDateTimeFieldValue(self, JpDockRpt.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, JpDockRpt.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, JpDockRpt.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, JpDockRpt.fields.updatedBy)
	}
}

object JpDockRpt extends StorableObject[JpDockRpt] {
	val entityName: String = "JP_DOCK_RPT"

	object fields extends FieldsObject {
		val drId = new IntDatabaseField(self, "DR_ID")
		val drDate = new NullableLocalDateTimeDatabaseField(self, "DR_DATE")
		val mercSo = new NullableDoubleDatabaseField(self, "MERC_SO")
		val kmSo = new NullableDoubleDatabaseField(self, "KM_SO")
		val laserSo = new NullableDoubleDatabaseField(self, "LASER_SO")
		val 420So = new NullableDoubleDatabaseField(self, "420_SO")
		val r19So = new NullableDoubleDatabaseField(self, "R19_SO")
		val sonarSo = new NullableDoubleDatabaseField(self, "SONAR_SO")
		val wsSo = new NullableDoubleDatabaseField(self, "WS_SO")
		val kayakSo = new NullableDoubleDatabaseField(self, "KAYAK_SO")
		val launchesAvail = new NullableStringDatabaseField(self, "LAUNCHES_AVAIL", 100)
		val capsizes = new NullableDoubleDatabaseField(self, "CAPSIZES")
		val maxWait = new NullableDoubleDatabaseField(self, "MAX_WAIT")
		val ra = new NullableDoubleDatabaseField(self, "RA")
		val totalCrew = new NullableDoubleDatabaseField(self, "TOTAL_CREW")
		val damage = new NullableStringDatabaseField(self, "DAMAGE", 4000)
		val injuries = new NullableStringDatabaseField(self, "INJURIES", 4000)
		val excursions = new NullableStringDatabaseField(self, "EXCURSIONS", 4000)
		val activities = new NullableStringDatabaseField(self, "ACTIVITIES", 4000)
		val notes = new NullableStringDatabaseField(self, "NOTES", 4000)
		val supSo = new NullableDoubleDatabaseField(self, "SUP_SO")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.drId
}