package org.sailcbi.APIServer.Test.IO.PreparedQueries


import com.coleji.framework.IO.PreparedQueries.{PreparedQueryForInsert, PreparedValue}
import org.junit.runner.RunWith
import org.sailcbi.APIServer.IO.PreparedQueries._
import org.sailcbi.APIServer.UserTypes.RootRequestCache
import org.sailcbi.APIServer.Services.Boot.ServerBootLoaderTest
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

import java.time.LocalDateTime

@RunWith(classOf[JUnitRunner])
class PreparedValueTest extends FunSuite {
	test("dfgh") {
		ServerBootLoaderTest.withPAWriteable(pa => {
			val rc = pa.assertRC(RootRequestCache, RootRequestCache.uniqueUserName)


			val ins = new PreparedQueryForInsert(Set(RootRequestCache)) {
				override val pkName: Option[String] = Some("PK")

				override val preparedParams: List[PreparedValue] = List(
					LocalDateTime.now().minusDays(6)
				)


				override def getQuery: String =
					"""
					  |insert into unit_test_scratch (col_date) values (?)
					  |""".stripMargin
			}

			rc.executePreparedQueryForInsert(ins)
		})
	}
//	def testPreparedDate[T](d: T, prepare: T => PreparedValue, getFromRSW: ResultSetWrapper => T)(pa: PermissionsAuthority): scalatest.Assertion = {
//		val rc = pa.assertRC(RootUserType.create)
//
//
//
//
//		val q = new PreparedQueryForInsert(Set(RootUserType)) {
//			override val preparedParams: List[PreparedValue] = List(prepare(d))
//			override val pkName: Option[String] = Some("PROMO_ID")
//
//			override def getQuery: String =
//				"""
//				  |insert into promotions (start_date) values (?)
//				  |""".stripMargin
//		}
//
//		val pk = rc.executePreparedQueryForInsert(q)
//
//		val sel = new PreparedQueryForSelect[T](Set(RootUserType)) {
//			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): T = getFromRSW(rsw)
//
//			override val preparedParams: List[PreparedValue] = List(pk.get)
//
//			override def getQuery: String =
//				"""
//				  |select start_date from promotions where promo_id = ?
//				  |""".stripMargin
//		}
//
//		val dSelected: T = rc.executePreparedQueryForSelect(sel).head
//
//		val del = new PreparedQueryForUpdateOrDelete(Set(RootUserType)) {
//			override def getQuery: String = "delete from promotions where promo_id = ?"
//			override val preparedParams = List(pk.get)
//		}
//
//		rc.executePreparedQueryForUpdateOrDelete(del)
//		println(d)
//		println(dSelected)
//		assert(d.equals(dSelected))
//	}

}
