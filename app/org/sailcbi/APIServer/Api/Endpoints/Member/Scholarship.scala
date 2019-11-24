package org.sailcbi.APIServer.Api.Endpoints.Member

import javax.inject.Inject
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.Entities.MagicIds
import org.sailcbi.APIServer.IO.PreparedQueries.{HardcodedQueryForSelect, HardcodedQueryForUpdateOrDelete, PreparedQueryForInsert, PreparedQueryForSelect}
import org.sailcbi.APIServer.Services.Authentication.MemberUserType
import org.sailcbi.APIServer.Services.Exception.UnauthorizedAccessException
import org.sailcbi.APIServer.Services._
import play.api.libs.json.JsValue
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext

class Scholarship @Inject()(implicit exec: ExecutionContext) extends Controller {
	val INFLATION_FACTOR = 1.05
	val EII_MAX = 82000
	def postNo()(implicit PA: PermissionsAuthority) = Action(request => {
		try {
			val logger = PA.logger
			val parsedRequest = ParsedRequest(request)
			val rc: RequestCache = PA.getRequestCacheMember(None, parsedRequest).get
			val pb: PersistenceBroker = rc.pb
			val cb: CacheBroker = rc.cb
			val personId = MemberUserType.getAuthedPersonId(rc.auth.userName, pb)
			val data = request.body.asJson
			Scholarship.setOthersNonCurrent(pb, personId)
			val jpPrice = Scholarship.getBaseJpPrice(pb)
			val insertQuery = new PreparedQueryForInsert(Set(MemberUserType)) {
				val params: List[String] = List(personId.toString)
				override val pkName: Option[String] = None

				override def getQuery: String =
					s"""
					   |insert into eii_responses
					   |(person_id, season, is_current, is_applying, computed_eii, COMPUTED_PRICE) values
					   | (?, util_pkg.get_current_season, 'Y', 'N', null, $jpPrice)
							 """.stripMargin
			}

			pb.executePreparedQueryForInsert(insertQuery)
			Ok("done")
		} catch {
			case _: UnauthorizedAccessException => Ok("Access Denied")
			case e: Throwable => {
				println(e)
				e.printStackTrace()
				Ok("Internal Error")
			}
		}
	})
	def postYes()(implicit PA: PermissionsAuthority) = Action { request =>
		try {
			val logger = PA.logger
			val parsedRequest = ParsedRequest(request)
			val rc: RequestCache = PA.getRequestCacheMember(None, parsedRequest).get
			val pb: PersistenceBroker = rc.pb
			val cb: CacheBroker = rc.cb
			val personId = MemberUserType.getAuthedPersonId(rc.auth.userName, pb)
			val data = request.body.asJson
			data match {
				case None => {
					println("no body")
					new Status(400)("no body")
				}
				case Some(v: JsValue) => {
					println(v)
					val parsed = ScholarshipYesShape.apply(v)
					Scholarship.setOthersNonCurrent(pb, personId)

					val adults = {
						if (parsed.numberWorkers < 1) 1
						else if (parsed.numberWorkers > 2) 2
						else parsed.numberWorkers
					}

					val children = {
						if (parsed.childCount < 1) 1
						else if (parsed.childCount > 6) 6
						else parsed.childCount
					}

					val eiiQ = new PreparedQueryForSelect[Double](Set(MemberUserType)) {
						def getQuery: String =
							s"""
							   |select eii
							   |from eii_mit
							   |where adults = ?
							   |and children = ?
							   |
						 """.stripMargin

						def mapResultSetRowToCaseObject(rs: ResultSetWrapper): Double = rs.getDouble(1)

						override val params: List[String] = List(adults.toString, children.toString)
					}
					val eiis = pb.executePreparedQueryForSelect(eiiQ)
					if (eiis.length == 1) {
						val myJpPrice = Scholarship.getMyJpPrice(pb, eiis.head, parsed.income, children)
						val insertQuery = new PreparedQueryForInsert(Set(MemberUserType)) {
							val params: List[String] = List(personId.toString)
							override val pkName: Option[String] = None

							override def getQuery: String =
								s"""
								   |insert into eii_responses
								   |(PERSON_ID,
								   |        SEASON,
								   |        WORKERS,
								   |        CHILDREN,
								   |        INCOME,
								   |        COMPUTED_EII,
								   |        COMPUTED_PRICE,
								   |        IS_APPLYING,
								   |        IS_CURRENT
								   |  ) values (
									   |  ?,
									   |  util_pkg.get_current_season,
									   |  ${parsed.numberWorkers},
									   |  ${parsed.childCount},
									   |  ${parsed.income},
									   |  ${eiis.head},
									   | $myJpPrice,
									   | 'Y',
									   | 'Y'
								   |  )
						 """.stripMargin
						}

						pb.executePreparedQueryForInsert(insertQuery)
					} else logger.error("Unable to find eii for response: " + parsed.toString)
					Ok("done")
				}
				case Some(v) => {
					println("wut dis " + v)
					Ok("wat")
				}
			}


			Ok("done")

		} catch {
			case _: UnauthorizedAccessException => Ok("Access Denied")
			case e: Throwable => {
				println(e)
				e.printStackTrace()
				Ok("Internal Error")
			}
		}
	}
}

case class Kids(infants: Int, preschoolers: Int, schoolagers: Int, teenagers: Int)


object Scholarship {
	def getBaseJpPrice(pb: PersistenceBroker): Double = {
		val q = new HardcodedQueryForSelect[Double](Set(MemberUserType)) {
			def getQuery: String =
				s"""
				   |select price from membership_types where membership_type_id = ${MagicIds.JUNIOR_SUMMER_MEMBERSHIP_TYPE_ID}
							 """.stripMargin

			def mapResultSetRowToCaseObject(rs: ResultSetWrapper): Double = rs.getDouble(1)
		}
		pb.executePreparedQueryForSelect(q).head
	}

	def reduceKids(kids: Kids): Kids = {
		val total = kids.infants + kids.preschoolers + kids.schoolagers + kids.teenagers
		if (total > 6) {
			if (kids.teenagers > 0) reduceKids(Kids(kids.infants, kids.preschoolers, kids.schoolagers, kids.teenagers-1))
			else if (kids.schoolagers > 0) reduceKids(Kids(kids.infants, kids.preschoolers, kids.schoolagers-1, kids.teenagers))
			else if (kids.preschoolers > 0) reduceKids(Kids(kids.infants, kids.preschoolers-1, kids.schoolagers, kids.teenagers))
			else if (kids.infants > 0) reduceKids(Kids(kids.infants-1, kids.preschoolers, kids.schoolagers, kids.teenagers))
			else Kids(0,0,0,0)
		} else kids
	}

	def getMyJpPrice(pb: PersistenceBroker, eii: Double, income: Double, totalKids: Int): Double = {
		println(s"calling get jp price with eii $eii, income $income, kids $totalKids")
		val q = new HardcodedQueryForSelect[Double](Set(MemberUserType)) {
			def getQuery: String =
				s"""
				   |select person_pkg.jp_price($eii, $income, $totalKids) from dual
				   |""".stripMargin

			def mapResultSetRowToCaseObject(rs: ResultSetWrapper): Double = rs.getDouble(1)
		}
		pb.executePreparedQueryForSelect(q).head
	}

	def setOthersNonCurrent(pb: PersistenceBroker, personId: Int): Unit = {
		val q = new HardcodedQueryForUpdateOrDelete(Set(MemberUserType)) {
			override def getQuery: String =
				s"""
				  |  update eii_responses set is_current = 'N'
				  |    where person_id = $personId
				  |    and season = util_pkg.get_current_season
				""".stripMargin
		}
		pb.executePreparedQueryForUpdateOrDelete(q)
	}
}