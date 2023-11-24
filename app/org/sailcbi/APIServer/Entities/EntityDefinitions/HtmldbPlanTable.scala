package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class HtmldbPlanTable extends StorableClass(HtmldbPlanTable) {
	override object values extends ValuesObject {
		val statementId = new NullableStringFieldValue(self, HtmldbPlanTable.fields.statementId)
		val planId = new NullableIntFieldValue(self, HtmldbPlanTable.fields.planId)
		val timestamp = new NullableDateTimeFieldValue(self, HtmldbPlanTable.fields.timestamp)
		val remarks = new NullableStringFieldValue(self, HtmldbPlanTable.fields.remarks)
		val operation = new NullableStringFieldValue(self, HtmldbPlanTable.fields.operation)
		val options = new NullableStringFieldValue(self, HtmldbPlanTable.fields.options)
		val objectNode = new NullableStringFieldValue(self, HtmldbPlanTable.fields.objectNode)
		val objectOwner = new NullableStringFieldValue(self, HtmldbPlanTable.fields.objectOwner)
		val objectName = new NullableStringFieldValue(self, HtmldbPlanTable.fields.objectName)
		val objectAlias = new NullableStringFieldValue(self, HtmldbPlanTable.fields.objectAlias)
		val objectInstance = new NullableDoubleFieldValue(self, HtmldbPlanTable.fields.objectInstance)
		val objectType = new NullableStringFieldValue(self, HtmldbPlanTable.fields.objectType)
		val optimizer = new NullableStringFieldValue(self, HtmldbPlanTable.fields.optimizer)
		val searchColumns = new NullableDoubleFieldValue(self, HtmldbPlanTable.fields.searchColumns)
		val id = new NullableIntFieldValue(self, HtmldbPlanTable.fields.id)
		val parentId = new NullableIntFieldValue(self, HtmldbPlanTable.fields.parentId)
		val depth = new NullableDoubleFieldValue(self, HtmldbPlanTable.fields.depth)
		val position = new NullableDoubleFieldValue(self, HtmldbPlanTable.fields.position)
		val cost = new NullableDoubleFieldValue(self, HtmldbPlanTable.fields.cost)
		val cardinality = new NullableDoubleFieldValue(self, HtmldbPlanTable.fields.cardinality)
		val bytes = new NullableDoubleFieldValue(self, HtmldbPlanTable.fields.bytes)
		val otherTag = new NullableStringFieldValue(self, HtmldbPlanTable.fields.otherTag)
		val partitionStart = new NullableStringFieldValue(self, HtmldbPlanTable.fields.partitionStart)
		val partitionStop = new NullableStringFieldValue(self, HtmldbPlanTable.fields.partitionStop)
		val partitionId = new NullableIntFieldValue(self, HtmldbPlanTable.fields.partitionId)
		val other = new NullableDoubleFieldValue(self, HtmldbPlanTable.fields.other)
		val distribution = new NullableStringFieldValue(self, HtmldbPlanTable.fields.distribution)
		val cpuCost = new NullableDoubleFieldValue(self, HtmldbPlanTable.fields.cpuCost)
		val ioCost = new NullableDoubleFieldValue(self, HtmldbPlanTable.fields.ioCost)
		val tempSpace = new NullableDoubleFieldValue(self, HtmldbPlanTable.fields.tempSpace)
		val accessPredicates = new NullableStringFieldValue(self, HtmldbPlanTable.fields.accessPredicates)
		val filterPredicates = new NullableStringFieldValue(self, HtmldbPlanTable.fields.filterPredicates)
		val projection = new NullableStringFieldValue(self, HtmldbPlanTable.fields.projection)
		val time = new NullableDoubleFieldValue(self, HtmldbPlanTable.fields.time)
		val qblockName = new NullableStringFieldValue(self, HtmldbPlanTable.fields.qblockName)
	}
}

object HtmldbPlanTable extends StorableObject[HtmldbPlanTable] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "HTMLDB_PLAN_TABLE"

	object fields extends FieldsObject {
		val statementId = new NullableStringDatabaseField(self, "STATEMENT_ID", 30)
		val planId = new NullableIntDatabaseField(self, "PLAN_ID")
		val timestamp = new NullableDateTimeDatabaseField(self, "TIMESTAMP")
		val remarks = new NullableStringDatabaseField(self, "REMARKS", 4000)
		val operation = new NullableStringDatabaseField(self, "OPERATION", 30)
		val options = new NullableStringDatabaseField(self, "OPTIONS", 255)
		val objectNode = new NullableStringDatabaseField(self, "OBJECT_NODE", 128)
		val objectOwner = new NullableStringDatabaseField(self, "OBJECT_OWNER", 128)
		val objectName = new NullableStringDatabaseField(self, "OBJECT_NAME", 128)
		val objectAlias = new NullableStringDatabaseField(self, "OBJECT_ALIAS", 261)
		val objectInstance = new NullableDoubleDatabaseField(self, "OBJECT_INSTANCE")
		val objectType = new NullableStringDatabaseField(self, "OBJECT_TYPE", 128)
		val optimizer = new NullableStringDatabaseField(self, "OPTIMIZER", 255)
		val searchColumns = new NullableDoubleDatabaseField(self, "SEARCH_COLUMNS")
		val id = new NullableIntDatabaseField(self, "ID")
		val parentId = new NullableIntDatabaseField(self, "PARENT_ID")
		val depth = new NullableDoubleDatabaseField(self, "DEPTH")
		val position = new NullableDoubleDatabaseField(self, "POSITION")
		val cost = new NullableDoubleDatabaseField(self, "COST")
		val cardinality = new NullableDoubleDatabaseField(self, "CARDINALITY")
		val bytes = new NullableDoubleDatabaseField(self, "BYTES")
		val otherTag = new NullableStringDatabaseField(self, "OTHER_TAG", 255)
		val partitionStart = new NullableStringDatabaseField(self, "PARTITION_START", 255)
		val partitionStop = new NullableStringDatabaseField(self, "PARTITION_STOP", 255)
		val partitionId = new NullableIntDatabaseField(self, "PARTITION_ID")
		val other = new NullableDoubleDatabaseField(self, "OTHER")
		val distribution = new NullableStringDatabaseField(self, "DISTRIBUTION", 30)
		val cpuCost = new NullableDoubleDatabaseField(self, "CPU_COST")
		val ioCost = new NullableDoubleDatabaseField(self, "IO_COST")
		val tempSpace = new NullableDoubleDatabaseField(self, "TEMP_SPACE")
		val accessPredicates = new NullableStringDatabaseField(self, "ACCESS_PREDICATES", 4000)
		val filterPredicates = new NullableStringDatabaseField(self, "FILTER_PREDICATES", 4000)
		val projection = new NullableStringDatabaseField(self, "PROJECTION", 4000)
		val time = new NullableDoubleDatabaseField(self, "TIME")
		val qblockName = new NullableStringDatabaseField(self, "QBLOCK_NAME", 128)
	}
}