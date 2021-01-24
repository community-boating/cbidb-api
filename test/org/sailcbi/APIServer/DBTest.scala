package org.sailcbi.APIServer

import org.junit.runner.RunWith
import org.sailcbi.APIServer.Entities.EntityDefinitions.{JpClassType, MembershipType}
import org.sailcbi.APIServer.IO.PreparedQueries.{PreparedQueryForInsert, PreparedQueryForUpdateOrDelete}
import org.sailcbi.APIServer.Services.Authentication.RootRequestCache
import org.sailcbi.APIServer.Services.Boot.ServerBootLoaderTest
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class DBTest extends FunSuite {
	test("dbaccess") {
		ServerBootLoaderTest.withPA(pa => {
			val rc = pa.assertRC(RootRequestCache.create)

			val types = rc.getAllObjectsOfClass(JpClassType)
			println(types)
		})
	}
	test("dbaccess2") {
		ServerBootLoaderTest.withPA(pa => {
			val rc = pa.assertRC(RootRequestCache.create)

			val types = rc.getAllObjectsOfClass(MembershipType)
			println(types)
		})
	}
	test("Writes should fail in test mode...") {
		ServerBootLoaderTest.withPA(pa => {
			val rc = pa.assertRC(RootRequestCache.create)


			assertThrows[AnyRef]({
				rc.executePreparedQueryForInsert(new PreparedQueryForInsert(Set(RootRequestCache)) {
					override val params: List[String] = List("Blah")
					override val pkName: Option[String] = Some("TYPE_ID")

					override def getQuery: String = "INSERT INTO JP_CLASS_TYPES (TYPE_NAME) VALUES (?)"
				})
			})
		})
	}
	test("... unless we use a writeable PA") {
		ServerBootLoaderTest.withPAWriteable(pa => {
			val rc = pa.assertRC(RootRequestCache.create)


			val typeName = "Blah333"

			rc.executePreparedQueryForInsert(new PreparedQueryForInsert(Set(RootRequestCache)) {
				override val params: List[String] = List(typeName)
				override val pkName: Option[String] = Some("TYPE_ID")

				override def getQuery: String = "INSERT INTO JP_CLASS_TYPES (TYPE_NAME) VALUES (?)"
			})
			val types = rc.getAllObjectsOfClass(JpClassType)
			println(types.size)

			rc.executePreparedQueryForUpdateOrDelete(new PreparedQueryForUpdateOrDelete(Set(RootRequestCache)) {
				override val params: List[String] = List(typeName)

				override def getQuery: String = "DELETE FROM JP_CLASS_TYPES WHERE TYPE_NAME = ?"
			})
			val types2 = rc.getAllObjectsOfClass(JpClassType)
			println(types2.size)
			assert(types.size == types2.size + 1)
		})
	}
	test("sdfgnjkdgfjk") {
		ServerBootLoaderTest.withPAWriteable(pa => {
			val rc = pa.assertRC(RootRequestCache.create)


			val q = new PreparedQueryForInsert(Set(RootRequestCache)) {
				override val params: List[String] = List(null)
				override val pkName: Option[String] = Some("PROMO_ID")

				override def getQuery: String =
					"""
					  |insert into promotions (start_date) values (?)
					  |""".stripMargin
			}

			rc.executePreparedQueryForInsert(q)
		})
	}
	test("just get some fields") {
		ServerBootLoaderTest.withPA(pa => {
			val rc = pa.assertRC(RootRequestCache.create)

			val types = rc.getAllObjectsOfClass(JpClassType, Some(List(JpClassType.fields.typeName)))
			val aType = types.head
			println(aType.values.typeName.get)
			assertThrows[AnyRef]({
				println(aType.values.typeId.get)
			})
		})
	}
}
