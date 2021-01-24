package org.sailcbi.APIServer

import org.junit.runner.RunWith
import org.sailcbi.APIServer.CbiUtil.DateUtil
import org.sailcbi.APIServer.IO.PreparedQueries._
import org.sailcbi.APIServer.Services.Authentication.RootUserType
import org.sailcbi.APIServer.Services.Boot.ServerBootLoaderTest
import org.sailcbi.APIServer.Services.{PermissionsAuthority, ResultSetWrapper}
import org.scalatest
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

import java.time.temporal.ChronoUnit
import java.time.{LocalDate, LocalDateTime, ZonedDateTime}

@RunWith(classOf[JUnitRunner])
class Temp extends FunSuite {
	def testPreparedDate[T](d: T, prepare: T => PreparedValue, getFromRSW: ResultSetWrapper => T)(pa: PermissionsAuthority): scalatest.Assertion = {
		val rc = pa.assertRC(RootUserType.create)




		val q = new PreparedQueryForInsert(Set(RootUserType)) {
			override val preparedParams: List[PreparedValue] = List(prepare(d))
			override val pkName: Option[String] = Some("PROMO_ID")

			override def getQuery: String =
				"""
				  |insert into promotions (start_date) values (?)
				  |""".stripMargin
		}

		val pk = rc.executePreparedQueryForInsert(q)

		val sel = new PreparedQueryForSelect[T](Set(RootUserType)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): T = getFromRSW(rsw)

			override val preparedParams: List[PreparedValue] = List(pk.get)

			override def getQuery: String =
				"""
				  |select start_date from promotions where promo_id = ?
				  |""".stripMargin
		}

		val dSelected: T = rc.executePreparedQueryForSelect(sel).head

		val del = new PreparedQueryForUpdateOrDelete(Set(RootUserType)) {
			override def getQuery: String = "delete from promotions where promo_id = ?"
			override val preparedParams = List(pk.get)
		}

		rc.executePreparedQueryForUpdateOrDelete(del)
		println(d)
		println(dSelected)
		assert(d.equals(dSelected))
	}
	test("PreparedLocalDate") {
		ServerBootLoaderTest.withPAWriteable(testPreparedDate(LocalDate.now(), PreparedLocalDate, _.getLocalDate(1)))
	}
	test("PreparedLocalDateTime") {
		ServerBootLoaderTest.withPAWriteable(testPreparedDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), PreparedLocalDateTime, _.getLocalDateTime(1)))
	}
	test("PreparedZonedDateTime") {
		ServerBootLoaderTest.withPAWriteable(testPreparedDate(
			ZonedDateTime.now().truncatedTo(ChronoUnit.SECONDS),
			PreparedZonedDateTime,
			rsw => DateUtil.toBostonTime(rsw.getLocalDateTime(1))
		))
	}
}
