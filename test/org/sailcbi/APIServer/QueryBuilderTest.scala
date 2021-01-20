package org.sailcbi.APIServer

import org.junit.runner.RunWith
import org.sailcbi.APIServer.Entities.EntityDefinitions.{JpClassInstance, JpClassSignup, JpClassType, JpClassWlResult}
import org.sailcbi.APIServer.Services.Authentication.RootUserType
import org.sailcbi.APIServer.Services.Boot.ServerBootLoaderTest
import org.sailcbi.APIServer.Storable.Fields.DatabaseField
import org.sailcbi.APIServer.Storable.Filter
import org.sailcbi.APIServer.Storable.StorableQuery._
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class QueryBuilderTest extends FunSuite {
	test("1 table, no filters (jp class types, typeID and typeName)") {
		ServerBootLoaderTest.withPA(pa => {
			val rc = pa.assertRC(RootUserType.create)
			val pb = rc.pb

			val types = TableAlias.wrapForInnerJoin(JpClassType)

			object columns {
				val typeId = JpClassType.fields.typeId.alias(types)
				val typeName = JpClassType.fields.typeName.alias(types)
			}

			val q = QueryBuilder
				.from(types)
				.select(List(columns.typeId, columns.typeName))

			val results = rc.executeQueryBuilder(q)
			results.foreach(row => {
				val typeId = row.getValue(columns.typeId)
				println( typeId + ":  " + row.getValue(columns.typeName))
			})
		})
	}

	test("2 tables, no filters (jp instances and types)") {
		ServerBootLoaderTest.withPA(pa => {
			val rc = pa.assertRC(RootUserType.create)
			val pb = rc.pb

			val types = TableAlias.wrapForInnerJoin(JpClassType)
			val instances = TableAlias.wrapForInnerJoin(JpClassInstance)

			object columns {
				val types_typeId = JpClassType.fields.typeId.alias(types)
				val instances_typeId = JpClassInstance.fields.typeId.alias(instances)
				val typeName = JpClassType.fields.typeName.alias(types)
				val instanceId = JpClassInstance.fields.instanceId.alias(instances)
				val testint = 1
			}
			val q = QueryBuilder
				.from(instances)
				.innerJoin(types, columns.instances_typeId.wrapFilter(_.equalsField(columns.types_typeId)))
				.select(QueryBuilder.colsAsList(columns))

			val results = rc.executeQueryBuilder(q)
			results.foreach(row => {
				val typeId = row.getValue(columns.types_typeId)
				val typeName = row.getValue(columns.typeName)
				val instanceId = row.getValue(columns.instanceId)
				println( instanceId + ":  " + typeId + "(" + typeName + ")")
			})
		})
	}

	test("2 tables, filters") {
		ServerBootLoaderTest.withPA(pa => {
			val rc = pa.assertRC(RootUserType.create)
			val pb = rc.pb

			val types = TableAlias.wrapForInnerJoin(JpClassType)
			val instances = TableAlias.wrapForInnerJoin(JpClassInstance)

			object columns {
				val types_typeId = JpClassType.fields.typeId.alias(types)
				val instances_typeId = JpClassInstance.fields.typeId.alias(instances)
		//		val typeName = JpClassType.fields.typeName.alias(types)
				val instanceId = JpClassInstance.fields.instanceId.alias(instances)
				val types_displayOrder = JpClassType.fields.displayOrder.alias(types)
				val testint = 1
			}
			val q = QueryBuilder
				.from(instances)
				.innerJoin(types, Filter.and(List(
					columns.instances_typeId.wrapFilter(_.equalsField(columns.types_typeId)),
					DatabaseField.testFilter("join1")
				)))
//				.leftOuterJoin(team, Filter.and(List(
//					columns.teamId.wrapFilter(_.equalsField(columns.types_typeId)),
//					DatabaseField.testFilter("join2")
//				)))
				.where(List(
					columns.types_typeId.wrapFilter(_.lessThanConstant(10)),
					columns.types_typeId.wrapFilter(_.lessThanConstant(30)),
					DatabaseField.testFilter("where1"),
					DatabaseField.testFilter("where2")
				))
				.select(QueryBuilder.colsAsList(columns))

			val results = rc.executeQueryBuilder(q)
			results.foreach(row => {
				val typeId = row.getValue(columns.types_typeId)
			//	val typeName = row.getValue(columns.typeName)
				val instanceId = row.getValue(columns.instanceId)
		//		val teamId = row.getValue(columns.teamId)
				println( instanceId + ":  " + typeId /*+ "(" + typeName + ")  - "*/ /*+ teamId*/)
				val classType = JpClassType.construct(row.ps)
				println("typeId: " + classType.values.typeId)
				println("typeName: " + classType.values.typeName)
				println("displayOrder: " + classType.values.displayOrder)
			})
		})
	}

	test("outer join") {
		ServerBootLoaderTest.withPA(pa => {
			val rc = pa.assertRC(RootUserType.create)
			val pb = rc.pb

			val types = TableAlias.wrapForInnerJoin(JpClassType)
			val instances = TableAlias.wrapForInnerJoin(JpClassInstance)
			val signups = TableAlias.wrapForInnerJoin(JpClassSignup)
			val wlResults = TableAlias.wrapForOuterJoin(JpClassWlResult)

			object columns {
				val types_typeId = JpClassType.fields.typeId.alias(types)
				val instances_typeId = JpClassInstance.fields.typeId.alias(instances)
				val typeName = JpClassType.fields.typeName.alias(types)
				val instances_instanceId = JpClassInstance.fields.instanceId.alias(instances)
				val types_displayOrder = JpClassType.fields.displayOrder.alias(types)
				val signups_signupId = JpClassSignup.fields.signupId.alias(signups)
				val signups_instanceId = JpClassSignup.fields.instanceId.alias(signups)
				val wlResults_signupId = JpClassWlResult.fields.signupId.alias(wlResults)
				val wlResult = JpClassWlResult.fields.wlResult.alias(wlResults)
				val testint = 1
			}
			val q = QueryBuilder
				.from(signups)
				.innerJoin(instances, Filter.and(List(
					columns.signups_instanceId.wrapFilter(_.equalsField(columns.instances_instanceId))
				)))
				.innerJoin(types, Filter.and(List(
					columns.instances_typeId.wrapFilter(_.equalsField(columns.types_typeId)),
					DatabaseField.testFilter("join1")
				)))
				.outerJoin(wlResults, Filter.and(List(
					columns.wlResults_signupId.wrapFilter(_.equalsField(columns.signups_signupId))
				)))
				.where(List(
					columns.types_typeId.wrapFilter(_.lessThanConstant(10)),
					columns.types_typeId.wrapFilter(_.lessThanConstant(30)),
					DatabaseField.testFilter("where1"),
					DatabaseField.testFilter("where2")
				))
				.select(QueryBuilder.colsAsList(columns))

			val results = rc.executeQueryBuilder(q)
			results.foreach(row => {
				val typeId = row.getValue(columns.types_typeId)
				val typeName = row.getValue(columns.typeName)
				val instanceId = row.getValue(columns.instances_instanceId)
				println( instanceId + ":  " + typeId /*+ "(" + typeName + ")  - "*/ /*+ teamId*/)
				val classType = JpClassType.construct(row.ps)
				println("typeId: " + classType.values.typeId)
				println("typeName: " + classType.values.typeName)
				println("displayOrder: " + classType.values.displayOrder)
				println("signupId from signups: " + row.getValue(columns.signups_signupId))
				println("signupId from wlresults: " + row.getValue(columns.wlResults_signupId))
				println("wlResult: " + row.getValue(columns.wlResult))
			})
		})
	}
}
