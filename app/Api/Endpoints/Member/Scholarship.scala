package Api.Endpoints.Member

import java.sql.ResultSet

import CbiUtil.ParsedRequest
import Entities.MagicIds
import IO.PreparedQueries.{HardcodedQueryForSelect, HardcodedQueryForUpdateOrDelete, PreparedQueryForInsert, PreparedQueryForSelect, PreparedQueryForUpdateOrDelete}
import Services.Authentication.MemberUserType
import Services.PermissionsAuthority.UnauthorizedAccessException
import Services.{CacheBroker, PermissionsAuthority, PersistenceBroker, RequestCache}
import javax.inject.Inject
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.ExecutionContext

class Scholarship @Inject()(implicit exec: ExecutionContext) extends Controller {
	val INFLATION_FACTOR = 1.05
	val EII_MAX = 82000
	def postNo() = Action(request => {
		try {
			val logger = PermissionsAuthority.logger
			val parsedRequest = ParsedRequest(request)
			val juniorId: Int = request.body.asJson.map(json => json("personId").toString().toInt).get
			val rc: RequestCache = PermissionsAuthority.getRequestCacheMemberWithJuniorId(None, parsedRequest, juniorId)._2.get
			val pb: PersistenceBroker = rc.pb
			val cb: CacheBroker = rc.cb
			val data = request.body.asJson
			data match {
				case None => {
					println("no body")
					new Status(400)("no body")
				}
				case Some(v: JsValue) => {
					val parsed = ScholarshipNoShape.apply(v)
					Scholarship.setOthersNonCurrent(pb, parsed.personId)
					val jpPrice = Scholarship.getBaseJpPrice(pb)
					val insertQuery = new PreparedQueryForInsert(Set(MemberUserType)) {
						val params: List[String] = List(parsed.personId.toString)
						override val pkName: Option[String] = None

						override def getQuery: String =
							s"""
							   |insert into eii_responses
							   |(person_id, season, is_current, is_applying, computed_eii, COMPUTED_PRICE) values
							   | (?, 2019, 'Y', 'N', null, $jpPrice)
							 """.stripMargin
					}

					pb.executePreparedQueryForInsert(insertQuery)
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
	})
	def postYes() = Action { request =>
		try {
			val logger = PermissionsAuthority.logger
			val parsedRequest = ParsedRequest(request)
			val juniorId: Int = request.body.asJson.map(json => json("personId").toString().toInt).get
			val rc: RequestCache = PermissionsAuthority.getRequestCacheMemberWithJuniorId(None, parsedRequest, juniorId)._2.get
			val pb: PersistenceBroker = rc.pb
			val cb: CacheBroker = rc.cb
			val data = request.body.asJson
			data match {
				case None => {
					println("no body")
					new Status(400)("no body")
				}
				case Some(v: JsValue) => {
					println(v)
					val parsed = ScholarshipYesShape.apply(v)
					Scholarship.setOthersNonCurrent(pb, parsed.personId)

					val reducedKids = Scholarship.reduceKids(Kids(parsed.infantCount, parsed.preschoolerCount, parsed.schoolagerCount, parsed.teenagerCount))
					val eiiQ = new HardcodedQueryForSelect[Double](Set(MemberUserType)) {
						def getQuery: String =
							s"""
							   |select $INFLATION_FACTOR * least($EII_MAX,(ANNUAL_TOTAL + 12*(PRECAUTIONARY + RETIREMENT + CHILDRENS_EDUCATION_TRAINING)))
							   |from eii
							   |where worker_ct = ${if (parsed.numberWorkers > 2) 2 else parsed.numberWorkers}
							   | and benefits = ${if (parsed.hasBenefits) "'Y'" else "'N'"}
							   | and infant_ct = ${reducedKids.infants}
							   | and preschooler_ct = ${reducedKids.preschoolers}
							   | and schoolage_ct = ${reducedKids.schoolagers}
							   | and teenage_ct = ${reducedKids.teenagers}
							   |
						 """.stripMargin

						def mapResultSetRowToCaseObject(rs: ResultSet): Double = rs.getDouble(1)
					}
					val eiis = pb.executePreparedQueryForSelect(eiiQ)
					if (eiis.length == 1) {
						val totalKids = parsed.infantCount + parsed.preschoolerCount + parsed.schoolagerCount + parsed.teenagerCount
						val myJpPrice = Scholarship.getMyJpPrice(pb, eiis.head, parsed.income, totalKids)
						val insertQuery = new PreparedQueryForInsert(Set(MemberUserType)) {
							val params: List[String] = List(parsed.personId.toString)
							override val pkName: Option[String] = None

							override def getQuery: String =
								s"""
								   |insert into eii_responses
								   |(PERSON_ID,
								   |        SEASON,
								   |        WORKERS,
								   |        BENEFITS,
								   |        INFANTS,
								   |        PRESCHOOLERS,
								   |        SCHOOLAGERS,
								   |        TEENAGERS,
								   |        INCOME,
								   |        COMPUTED_EII,
								   |        COMPUTED_PRICE,
								   |        IS_APPLYING,
								   |        IS_CURRENT,
								   |        utilized_inflation_factor
								   |  ) values (
									   |  ?,
									   |  2019,
									   |  ${parsed.numberWorkers},
									   |  ${if (parsed.hasBenefits) "'Y'" else "'N'"},
									   |  ${parsed.infantCount},
									   |  ${parsed.preschoolerCount},
									   |  ${parsed.schoolagerCount},
									   |  ${parsed.teenagerCount},
									   |  ${parsed.income},
									   |  ${eiis.head},
									   | $myJpPrice,
									   | 'Y',
									   | 'Y',
									   | $INFLATION_FACTOR
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

			def mapResultSetRowToCaseObject(rs: ResultSet): Double = rs.getDouble(1)
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
		val q = new HardcodedQueryForSelect[Double](Set(MemberUserType)) {
			def getQuery: String =
				s"""
				   |select person_pkg.jp_price($eii, $income, $totalKids) from dual
				   |""".stripMargin

			def mapResultSetRowToCaseObject(rs: ResultSet): Double = rs.getDouble(1)
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