package org.sailcbi.APIServer

import com.coleji.neptune.Core.RootRequestCache
import com.coleji.neptune.IO.PreparedQueries.{PreparedQueryForInsert, PreparedQueryForUpdateOrDelete}
import org.junit.runner.RunWith
import org.sailcbi.APIServer.Entities.EntityDefinitions.{JpClassType, MembershipType}
import org.sailcbi.APIServer.Server.CBIBootLoaderTest
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

import javax.inject.Inject

@RunWith(classOf[JUnitRunner])
class DBTest @Inject()(loader: CBIBootLoaderTest) extends FunSuite {
	test("dbaccess") {
		loader.withPA(pa => {
			val rc = loader.assertRC(pa)(RootRequestCache, RootRequestCache.uniqueUserName)

			val types = rc.getAllObjectsOfClass(JpClassType, Set(JpClassType.primaryKey))
			println(types)
		})
	}
	test("dbaccess2") {
		loader.withPA(pa => {
			val rc = loader.assertRC(pa)(RootRequestCache, RootRequestCache.uniqueUserName)

			val types = rc.getAllObjectsOfClass(MembershipType, Set(MembershipType.primaryKey))
			println(types)
		})
	}
	test("Writes should fail in test mode...") {
		loader.withPA(pa => {
			val rc = loader.assertRC(pa)(RootRequestCache, RootRequestCache.uniqueUserName)


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
		loader.withPAWriteable(pa => {
			val rc = loader.assertRC(pa)(RootRequestCache, RootRequestCache.uniqueUserName)


			val typeName = "Blah333"

			rc.executePreparedQueryForInsert(new PreparedQueryForInsert(Set(RootRequestCache)) {
				override val params: List[String] = List(typeName)
				override val pkName: Option[String] = Some("TYPE_ID")

				override def getQuery: String = "INSERT INTO JP_CLASS_TYPES (TYPE_NAME) VALUES (?)"
			})
			val types = rc.getAllObjectsOfClass(JpClassType, Set(JpClassType.primaryKey))
			println(types.size)

			rc.executePreparedQueryForUpdateOrDelete(new PreparedQueryForUpdateOrDelete(Set(RootRequestCache)) {
				override val params: List[String] = List(typeName)

				override def getQuery: String = "DELETE FROM JP_CLASS_TYPES WHERE TYPE_NAME = ?"
			})
			val types2 = rc.getAllObjectsOfClass(JpClassType, Set(JpClassType.primaryKey))
			println(types2.size)
			assert(types.size == types2.size + 1)
		})
	}
	test("sdfgnjkdgfjk") {
		loader.withPAWriteable(pa => {
			val rc = loader.assertRC(pa)(RootRequestCache, RootRequestCache.uniqueUserName)


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
		loader.withPA(pa => {
			val rc = loader.assertRC(pa)(RootRequestCache, RootRequestCache.uniqueUserName)

			val types = rc.getAllObjectsOfClass(JpClassType, Set(JpClassType.fields.typeName))
			val aType = types.head
			println(aType.values.typeName.get)
			assertThrows[AnyRef]({
				println(aType.values.typeId.get)
			})
		})
	}
}
