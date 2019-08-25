package org.sailcbi.APIServer

import org.junit.runner.RunWith
import org.sailcbi.APIServer.CbiUtil.CurrencyFormat
import org.sailcbi.APIServer.Entities.EntityDefinitions.{JpClassInstance, JpClassType, MembershipType}
import org.sailcbi.APIServer.IO.PreparedQueries.{HardcodedQueryForInsert, PreparedQueryForInsert, PreparedQueryForUpdateOrDelete}
import org.sailcbi.APIServer.Services.Authentication.{AuthenticationInstance, RootUserType}
import org.sailcbi.APIServer.Services.Boot.ServerBootLoaderTest
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class DBTest extends FunSuite {
	test("dbaccess") {
		ServerBootLoaderTest.withPA(pa => {
			val rc = pa.assertRC(AuthenticationInstance.ROOT)
			val pb = rc.pb
			val types = pb.getAllObjectsOfClass(JpClassType)
			println(types)
		})
	}
	test("dbaccess2") {
		ServerBootLoaderTest.withPA(pa => {
			val rc = pa.assertRC(AuthenticationInstance.ROOT)
			val pb = rc.pb
			val types = pb.getAllObjectsOfClass(MembershipType)
			println(types)
		})
	}
	test("Writes should fail in test mode...") {
		ServerBootLoaderTest.withPA(pa => {
			val rc = pa.assertRC(AuthenticationInstance.ROOT)
			val pb = rc.pb

			assertThrows[AnyRef]({
				pb.executePreparedQueryForInsert(new PreparedQueryForInsert(Set(RootUserType)) {
					override val params: List[String] = List("Blah")
					override val pkName: Option[String] = Some("TYPE_ID")

					override def getQuery: String = "INSERT INTO JP_CLASS_TYPES (TYPE_NAME) VALUES (?)"
				})
			})
		})
	}
	test("... unless we use a writeable PA") {
		ServerBootLoaderTest.withPAWriteable(pa => {
			val rc = pa.assertRC(AuthenticationInstance.ROOT)
			val pb = rc.pb

			val typeName = "Blah333"

			pb.executePreparedQueryForInsert(new PreparedQueryForInsert(Set(RootUserType)) {
				override val params: List[String] = List(typeName)
				override val pkName: Option[String] = Some("TYPE_ID")

				override def getQuery: String = "INSERT INTO JP_CLASS_TYPES (TYPE_NAME) VALUES (?)"
			})
			val types = pb.getAllObjectsOfClass(JpClassType)
			println(types.size)

			pb.executePreparedQueryForUpdateOrDelete(new PreparedQueryForUpdateOrDelete(Set(RootUserType)) {
				override val params: List[String] = List(typeName)

				override def getQuery: String = "DELETE FROM JP_CLASS_TYPES WHERE TYPE_NAME = ?"
			})
			val types2 = pb.getAllObjectsOfClass(JpClassType)
			println(types2.size)
		})
	}
}
