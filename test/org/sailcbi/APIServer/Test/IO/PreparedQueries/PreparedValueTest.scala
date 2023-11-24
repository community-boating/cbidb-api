package org.sailcbi.APIServer.Test.IO.PreparedQueries


import com.coleji.neptune.Core.RootRequestCache
import com.coleji.neptune.IO.PreparedQueries.{PreparedQueryForInsert, PreparedValue}
import org.junit.runner.RunWith
import org.sailcbi.APIServer.Server.CBIBootLoaderTest
import org.scalatest.funsuite.AnyFunSuite
import org.scalatestplus.junit.JUnitRunner

import java.time.LocalDateTime
import javax.inject.Inject

@RunWith(classOf[JUnitRunner])
class PreparedValueTest @Inject()(loader: CBIBootLoaderTest) extends AnyFunSuite {
	test("dfgh") {
		loader.withPAWriteable(pa => {
			val rc = loader.assertRC(pa)(RootRequestCache, RootRequestCache.uniqueUserName)


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
//		val rc = loader.assertRC(pa)(RootUserType.create)
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
