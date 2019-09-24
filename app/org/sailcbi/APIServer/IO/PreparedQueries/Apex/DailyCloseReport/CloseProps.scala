package org.sailcbi.APIServer.IO.PreparedQueries.Apex.DailyCloseReport

import java.sql.ResultSet
import java.time.ZonedDateTime

import org.sailcbi.APIServer.CbiUtil.{Currency, DateUtil}
import org.sailcbi.APIServer.IO.PreparedQueries.HardcodedQueryForSelect
import org.sailcbi.APIServer.Services.Authentication.ApexUserType
import org.sailcbi.APIServer.Services.ResultSetWrapper

class CloseProps(closeId: Int) extends HardcodedQueryForSelect[ClosePropsResult](Set(ApexUserType)) {
	val getQuery: String =
		s"""
		   |select
		   |c.close_id,
		   |v.GET_CASH_TOTAL,
		   |v.GET_INPERSON_AR_TOTAL,
		   |v.GET_CHECK_TOTAL,
		   |v.GET_INPERSON_TALLY,
		   |v.GET_ONLINE_CC_TOTAL,
		   |v.GET_ONLINE_AR,
		   |v.GET_ONLINE_TALLY,
		   |c.closed_datetime,
		   |c.tape_value,
		   |c.notes,
		   |u.user_id,
		   |u.name_first,
		   |u.name_last,
		   |nvl(cap.amount,0)
		   |from close_pkg_view v, fo_closes c, users u, close_ar_payments cap
		   |where v.close_id = c.close_id
		   |and c.finalized_by = u.user_id (+)
		   |and c.close_id = cap.close_id (+)
		   |and c.close_id = $closeId
		   |
    """.stripMargin

	override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): ClosePropsResult = new ClosePropsResult(
		rs.getInt(1),
		Currency.dollars(rs.getDouble(2)),
		Currency.dollars(rs.getDouble(3)),
		Currency.dollars(rs.getDouble(4)),
		Currency.dollars(rs.getDouble(5)),
		Currency.dollars(rs.getDouble(6)),
		Currency.dollars(rs.getDouble(7)),
		Currency.dollars(rs.getDouble(8)),
		rs.getOptionLocalDateTime(9).map(DateUtil.toBostonTime),
		Currency.dollars(rs.getOptionDouble(10).getOrElse(0d)),
		rs.getOptionString(11),
		rs.getOptionString(13).flatMap(firstName => rs.getOptionString(14).map(lastName => firstName + " " + lastName)),
		Currency.dollars(rs.getDouble(15))
	)
}

class ClosePropsResult(
							  val closeId: Int,
							  val cashTotal: Currency,
							  val inPersonARTotal: Currency,
							  val checkTotal: Currency,
							  val inPersonTally: Currency,
							  val onlineCCTotal: Currency,
							  val onlineAR: Currency,
							  val onlineTally: Currency,
							  val closedDatetime: Option[ZonedDateTime],
							  val tapeValue: Currency,
							  val notes: Option[String],
							  val finalizedBy: Option[String],
							  val arPaymentsTotal: Currency
					  ) {
	val inPersonRevenueTotal: Currency = cashTotal + inPersonARTotal + checkTotal
}