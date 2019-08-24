package org.sailcbi.APIServer.IO.PreparedQueries.Apex

import java.sql.ResultSet
import java.time.LocalDate

import org.sailcbi.APIServer.IO.PreparedQueries.HardcodedQueryForSelect
import org.sailcbi.APIServer.Services.Authentication.ApexUserType

class GetCurrentOnlineClose extends HardcodedQueryForSelect[GetCurrentOnlineCloseResult](Set(ApexUserType)) {
	val getQuery: String =
		s"""
		   |select close_id, created_on, closed_datetime from fo_closes c, current_closes cur where close_id = online_close
		   |
    """.stripMargin

	override def mapResultSetRowToCaseObject(rs: ResultSet): GetCurrentOnlineCloseResult = GetCurrentOnlineCloseResult(
		rs.getInt(1),
		rs.getDate(2).toLocalDate,
		try {
			Some(rs.getDate(3).toLocalDate)
		} catch {
			case _: Throwable => None
		},
	)
}

case class GetCurrentOnlineCloseResult(
	closeId: Int,
	createdOn: LocalDate,
	finalized: Option[LocalDate]
)
