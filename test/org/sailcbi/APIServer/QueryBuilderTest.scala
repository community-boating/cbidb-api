package org.sailcbi.APIServer

import org.junit.runner.RunWith
import org.sailcbi.APIServer.Entities.EntityDefinitions.{JpClassInstance, JpClassType}
import org.sailcbi.APIServer.Services.Authentication.AuthenticationInstance
import org.sailcbi.APIServer.Services.Boot.ServerBootLoaderTest
import org.sailcbi.APIServer.Storable.StorableQuery._
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class QueryBuilderTest extends FunSuite {
	test("1 table, no filters") {
		ServerBootLoaderTest.withPA(pa => {
			val rc = pa.assertRC(AuthenticationInstance.ROOT)
			val pb = rc.pb

			val types = TableAlias.wrap(JpClassType)

			object columns {
				val typeId = JpClassType.fields.typeId.alias(types)
				val typeName = JpClassType.fields.typeName.alias(types)
			}

			val q = QueryBuilder(
				fields=List(columns.typeId, columns.typeName),
				joinPoints = List(),
				filters=List()
			)

			val results = pb.executeQueryBuilder(q)
			results.foreach(row => {
				val typeId = row.getValue(columns.typeId)
				println( typeId + ":  " + row.getValue(columns.typeName))
			})
		})
	}

	test("2 tables, no filters") {
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
			}

			val q = QueryBuilder(
				fields=List(columns.types_typeId, columns.typeName, columns.instanceId),
				joinPoints = List(JoinPoint(columns.types_typeId, columns.instances_typeId)),
				filters=List()
			)

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

			object columns {
				val types_typeId = JpClassType.fields.typeId.alias(types)
				val instances_typeId = JpClassInstance.fields.typeId.alias(instances)
				val typeName = JpClassType.fields.typeName.alias(types)
				val instanceId = JpClassInstance.fields.instanceId.alias(instances)
			}

			val q = QueryBuilder(
				fields=List(columns.types_typeId, columns.typeName, columns.instanceId),
				joinPoints = List(JoinPoint(columns.types_typeId, columns.instances_typeId)),
				filters=List(columns.types_typeId.wrapFilter(_.lessThanConstant(10)))
			)

			val results = pb.executeQueryBuilder(q)
			results.foreach(row => {
				val typeId = row.getValue(columns.types_typeId)
				val typeName = row.getValue(columns.typeName)
				val instanceId = row.getValue(columns.instanceId)
				println( instanceId + ":  " + typeId + "(" + typeName + ")")
			})
		})
	}
}
