package org.sailcbi.APIServer.Test.IO.PreparedQueries


import java.time.temporal.ChronoUnit
import java.time.{LocalDate, LocalDateTime, ZonedDateTime}

import org.junit.runner.RunWith
import org.sailcbi.APIServer.CbiUtil.DateUtil
import org.sailcbi.APIServer.IO.PreparedQueries._
import org.sailcbi.APIServer.Services.Authentication.{AuthenticationInstance, RootUserType}
import org.sailcbi.APIServer.Services.Boot.ServerBootLoaderTest
import org.sailcbi.APIServer.Services.{PermissionsAuthority, ResultSetWrapper}
import org.scalatest
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class PreparedValueTest extends FunSuite {
	test("dfgh") {
		ServerBootLoaderTest.withPAWriteable(pa => {
			val rc = pa.assertRC(AuthenticationInstance.ROOT)
			val pb = rc.pb

			val ins = new PreparedQueryForInsert(Set(RootUserType)) {
				override val pkName: Option[String] = Some("PK")

				override val preparedParams: List[PreparedValue] = List(
					LocalDateTime.now().minusDays(6)
				)


				override def getQuery: String =
					"""
					  |insert into unit_test_scratch (col_date) values (?)
					  |""".stripMargin
			}

			pb.executePreparedQueryForInsert(ins)
		})
	}
//	def testPreparedDate[T](d: T, prepare: T => PreparedValue, getFromRSW: ResultSetWrapper => T)(pa: PermissionsAuthority): scalatest.Assertion = {
//		val rc = pa.assertRC(AuthenticationInstance.ROOT)
//		val pb = rc.pb
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
//		val pk = pb.executePreparedQueryForInsert(q)
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
//		val dSelected: T = pb.executePreparedQueryForSelect(sel).head
//
//		val del = new PreparedQueryForUpdateOrDelete(Set(RootUserType)) {
//			override def getQuery: String = "delete from promotions where promo_id = ?"
//			override val preparedParams = List(pk.get)
//		}
//
//		pb.executePreparedQueryForUpdateOrDelete(del)
//		println(d)
//		println(dSelected)
//		assert(d.equals(dSelected))
//	}

}
