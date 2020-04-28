package org.sailcbi.APIServer

import java.time.{LocalDate, LocalDateTime, ZonedDateTime}
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

import org.junit.runner.RunWith
import org.sailcbi.APIServer.CbiUtil.{CurrencyFormat, DateUtil}
import org.sailcbi.APIServer.Entities.EntityDefinitions.{JpClassInstance, JpClassType, MembershipType}
import org.sailcbi.APIServer.IO.PreparedQueries.{HardcodedQueryForInsert, PreparedInt, PreparedLocalDate, PreparedLocalDateTime, PreparedQueryForInsert, PreparedQueryForSelect, PreparedQueryForUpdateOrDelete, PreparedString, PreparedValue, PreparedZonedDateTime}
import org.sailcbi.APIServer.Services.Authentication.{AuthenticationInstance, RootUserType}
import org.sailcbi.APIServer.Services.Boot.ServerBootLoaderTest
import org.sailcbi.APIServer.Services.{PermissionsAuthority, ResultSetWrapper}
import org.scalatest
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class Temp extends FunSuite {
	def testPreparedDate[T](d: T, prepare: T => PreparedValue, getFromRSW: ResultSetWrapper => T)(pa: PermissionsAuthority): scalatest.Assertion = {
		val rc = pa.assertRC(AuthenticationInstance.ROOT)
		val pb = rc.pb



		val q = new PreparedQueryForInsert(Set(RootUserType)) {
			override val preparedParams: List[PreparedValue] = List(prepare(d))
			override val pkName: Option[String] = Some("PROMO_ID")

			override def getQuery: String =
				"""
				  |insert into promotions (start_date) values (?)
				  |""".stripMargin
		}

		val pk = pb.executePreparedQueryForInsert(q)

		val sel = new PreparedQueryForSelect[T](Set(RootUserType)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): T = getFromRSW(rsw)

			override val preparedParams: List[PreparedValue] = List(pk.get)

			override def getQuery: String =
				"""
				  |select start_date from promotions where promo_id = ?
				  |""".stripMargin
		}

		val dSelected: T = pb.executePreparedQueryForSelect(sel).head

		val del = new PreparedQueryForUpdateOrDelete(Set(RootUserType)) {
			override def getQuery: String = "delete from promotions where promo_id = ?"
			override val preparedParams = List(pk.get)
		}

		pb.executePreparedQueryForUpdateOrDelete(del)
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