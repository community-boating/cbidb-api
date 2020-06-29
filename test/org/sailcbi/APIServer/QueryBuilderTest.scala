package org.sailcbi.APIServer

import org.junit.runner.RunWith
import org.sailcbi.APIServer.Entities.EntityDefinitions.{JpClassInstance, JpClassType, JpTeam}
import org.sailcbi.APIServer.Services.Authentication.AuthenticationInstance
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
			val rc = pa.assertRC(AuthenticationInstance.ROOT)
			val pb = rc.pb

			val types = TableAlias.wrap(JpClassType)

			object columns {
				val typeId = JpClassType.fields.typeId.alias(types)
				val typeName = JpClassType.fields.typeName.alias(types)
			}

			val q = QueryBuilder
				.from(types)
				.select(List(columns.typeId, columns.typeName))

			val results = pb.executeQueryBuilder(q)
			results.foreach(row => {
				val typeId = row.getValue(columns.typeId)
				println( typeId + ":  " + row.getValue(columns.typeName))
			})
		})
	}

	test("2 tables, no filters (jp instances and types)") {
		ServerBootLoaderTest.withPA(pa => {
			val rc = pa.assertRC(AuthenticationInstance.ROOT)
			val pb = rc.pb

			val types = TableAlias.wrap(JpClassType)
			val instances = TableAlias.wrap(JpClassInstance)

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

			val results = pb.executeQueryBuilder(q)
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
			val rc = pa.assertRC(AuthenticationInstance.ROOT)
			val pb = rc.pb

			val types = TableAlias.wrap(JpClassType)
			val instances = TableAlias.wrap(JpClassInstance)
			val team = TableAlias.wrap(JpTeam)

			object columns {
				val types_typeId = JpClassType.fields.typeId.alias(types)
				val instances_typeId = JpClassInstance.fields.typeId.alias(instances)
		//		val typeName = JpClassType.fields.typeName.alias(types)
				val instanceId = JpClassInstance.fields.instanceId.alias(instances)
				val teamId = JpTeam.fields.teamId.alias(team)
				val types_displayOrder = JpClassType.fields.displayOrder.alias(types)
				val testint = 1
			}
			val q = QueryBuilder
				.from(instances)
				.innerJoin(types, Filter.and(List(
					columns.instances_typeId.wrapFilter(_.equalsField(columns.types_typeId)),
					DatabaseField.testFilter("join1")
				)))
				.leftOuterJoin(team, Filter.and(List(
					columns.teamId.wrapFilter(_.equalsField(columns.types_typeId)),
					DatabaseField.testFilter("join2")
				)))
				.where(List(
					columns.types_typeId.wrapFilter(_.lessThanConstant(10)),
					columns.types_typeId.wrapFilter(_.lessThanConstant(30)),
					DatabaseField.testFilter("where1"),
					DatabaseField.testFilter("where2")
				))
				.select(QueryBuilder.colsAsList(columns))

			val results = pb.executeQueryBuilder(q)
			results.foreach(row => {
				val typeId = row.getValue(columns.types_typeId)
			//	val typeName = row.getValue(columns.typeName)
				val instanceId = row.getValue(columns.instanceId)
		//		val teamId = row.getValue(columns.teamId)
				println( instanceId + ":  " + typeId /*+ "(" + typeName + ")  - "*/ /*+ teamId*/)
				val classType = JpClassType.construct(row.ps, rc)
				println("typeId: " + classType.values.typeId)
				println("typeName: " + classType.values.typeName)
				println("displayOrder: " + classType.values.displayOrder)
			})
		})
	}
}
