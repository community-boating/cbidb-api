package org.sailcbi.APIServer

import com.coleji.neptune.Core.{PermissionsAuthority, RootRequestCache}
import com.coleji.neptune.IO.PreparedQueries._
import com.coleji.neptune.Storable.ResultSetWrapper
import com.coleji.neptune.Util.DateUtil
import org.junit.runner.RunWith
import org.sailcbi.APIServer.Server.CBIBootLoaderTest
import org.scalatest
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

import java.time.temporal.ChronoUnit
import java.time.{LocalDate, LocalDateTime, ZonedDateTime}
import javax.inject.Inject

@RunWith(classOf[JUnitRunner])
class Temp @Inject()(loader: CBIBootLoaderTest) extends FunSuite {
	def testPreparedDate[T](d: T, prepare: T => PreparedValue, getFromRSW: ResultSetWrapper => T)(pa: PermissionsAuthority): scalatest.Assertion = {
		val rc = loader.assertRC(pa)(RootRequestCache, RootRequestCache.uniqueUserName)

		val q = new PreparedQueryForInsert(Set(RootRequestCache)) {
			override val preparedParams: List[PreparedValue] = List(prepare(d))
			override val pkName: Option[String] = Some("PROMO_ID")

			override def getQuery: String =
				"""
				  |insert into promotions (start_date) values (?)
				  |""".stripMargin
		}

		val pk = rc.executePreparedQueryForInsert(q)

		val sel = new PreparedQueryForSelect[T](Set(RootRequestCache)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): T = getFromRSW(rsw)

			override val preparedParams: List[PreparedValue] = List(pk.get)

			override def getQuery: String =
				"""
				  |select start_date from promotions where promo_id = ?
				  |""".stripMargin
		}

		val dSelected: T = rc.executePreparedQueryForSelect(sel).head

		val del = new PreparedQueryForUpdateOrDelete(Set(RootRequestCache)) {
			override def getQuery: String = "delete from promotions where promo_id = ?"
			override val preparedParams = List(pk.get)
		}

		rc.executePreparedQueryForUpdateOrDelete(del)
		println(d)
		println(dSelected)
		assert(d.equals(dSelected))
	}
	test("PreparedLocalDate") {
		loader.withPAWriteable(testPreparedDate(LocalDate.now(), PreparedLocalDate, _.getLocalDate(1)))
	}
	test("PreparedLocalDateTime") {
		loader.withPAWriteable(testPreparedDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), PreparedLocalDateTime, _.getLocalDateTime(1)))
	}
	test("PreparedZonedDateTime") {
		loader.withPAWriteable(testPreparedDate(
			ZonedDateTime.now().truncatedTo(ChronoUnit.SECONDS),
			PreparedZonedDateTime,
			rsw => DateUtil.toBostonTime(rsw.getLocalDateTime(1))
		))
	}
}
