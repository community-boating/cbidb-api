package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues.{DateTimeFieldValue, IntFieldValue, StringFieldValue}
import com.coleji.neptune.Storable.Fields.{DateTimeDatabaseField, IntDatabaseField, StringDatabaseField}
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.DateUtil
import play.api.libs.json.{JsString, JsValue}

import java.time.LocalDateTime

class JpClassWlResult extends StorableClass(JpClassWlResult) {
	object values extends ValuesObject {
		val signupId = new IntFieldValue(self, JpClassWlResult.fields.signupId)
		val wlResult = new StringFieldValue(self, JpClassWlResult.fields.wlResult)
		val offerExpDatetime = new DateTimeFieldValue(self, JpClassWlResult.fields.offerExpDatetime)
	}

	def getDisplayStatus: String = {
//		(case
//		when wl_result = 'F' then 'No Longer Eligible'
//		when wl_result = 'E' then 'Enrolled from Wait List'
//		when (wl_result = 'P' and offer_exp_datetime >= util_pkg.get_sysdate) then 'Offer Pending (expires '||to_char(wlr.offer_exp_datetime,'MM/DD/YYYY HH:MIPM')||')'
//		when (wl_result = 'P' and offer_exp_datetime < util_pkg.get_sysdate) then 'Offer Expired'
//		when (wl_result = 'A' and preapproved_exp >= util_pkg.get_sysdate) then 'Preapproved through '||to_char(preapproved_exp,'MM/DD/YYYY')
//		when (wl_result = 'A' and preapproved_exp < util_pkg.get_sysdate) then 'Preapproved (Expired)'
//		else ''
//		end) as wait_list_status,
		val result = this.values.wlResult.get
		if (result == "F") {
			"No Longer Eligible"
		} else if (result == "E") {
			"Enrolled from Wait List"
		} else if (result == "P" ) {
			val offerExp = this.values.offerExpDatetime.get
			if (offerExp.isBefore(LocalDateTime.now)) {
				"Offer Expired"
			} else {
				s"Offer Pending (expires ${offerExp.format(DateUtil.DATE_TIME_FORMATTER)})"
			}
		} else {
			result
		}
	}

	override def extraFieldsForJSValue: Map[String, JsValue] = Map(
		"statusString" -> JsString(this.getDisplayStatus)
	)
}

object JpClassWlResult extends StorableObject[JpClassWlResult] {
	val entityName: String = "JP_CLASS_WL_RESULTS"

	object fields extends FieldsObject {
		val signupId = new IntDatabaseField(self, "SIGNUP_ID")
		val wlResult = new StringDatabaseField(self, "WL_RESULT", 1)
		val offerExpDatetime = new DateTimeDatabaseField(self, "OFFER_EXP_DATETIME")
	}

	def primaryKey: IntDatabaseField = fields.signupId
}