package org.sailcbi.APIServer.IO.PreparedQueries.Apex.DailyCloseReport

import java.sql.ResultSet
import java.time.ZonedDateTime

import org.sailcbi.APIServer.CbiUtil.{Currency, DateUtil}
import org.sailcbi.APIServer.IO.PreparedQueries.HardcodedQueryForSelect
import org.sailcbi.APIServer.Services.Authentication.ApexUserType

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

	override def mapResultSetRowToCaseObject(rs: ResultSet): ClosePropsResult = new ClosePropsResult(
		rs.getInt(1),
		Currency.dollars(rs.getDouble(2)),
		Currency.dollars(rs.getDouble(3)),
		Currency.dollars(rs.getDouble(4)),
		Currency.dollars(rs.getDouble(5)),
		Currency.dollars(rs.getDouble(6)),
		Currency.dollars(rs.getDouble(7)),
		Currency.dollars(rs.getDouble(8)),
		{
			val ret = rs.getTimestamp(9)
			if (rs.wasNull()) None
			else Some(DateUtil.toBostonTime(ret.toLocalDateTime))
		},
		Currency.dollars(rs.getDouble(10)),
		{
			val notes = rs.getString(11); if (rs.wasNull()) None else Some(notes)
		},
		{
			val firstName = rs.getString(13)
			val firstNameWasNull = rs.wasNull()
			val lastName = rs.getString(14)
			if (rs.wasNull() || firstNameWasNull) None
			else Some(firstName + " " + lastName)
		},
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