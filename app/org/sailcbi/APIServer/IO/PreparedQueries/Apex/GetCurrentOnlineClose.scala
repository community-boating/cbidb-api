package org.sailcbi.APIServer.IO.PreparedQueries.Apex

import java.time.LocalDate

import org.sailcbi.APIServer.IO.PreparedQueries.HardcodedQueryForSelect
import org.sailcbi.APIServer.Services.Authentication.ApexUserType
import org.sailcbi.APIServer.Services.ResultSetWrapper

class GetCurrentOnlineClose extends HardcodedQueryForSelect[GetCurrentOnlineCloseResult](Set(ApexUserType)) {
	val getQuery: String =
		s"""
		   |select close_id, created_on, closed_datetime from fo_closes c, current_closes cur where close_id = online_close
		   |
    """.stripMargin

	override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): GetCurrentOnlineCloseResult = GetCurrentOnlineCloseResult(
		rs.getInt(1),
		rs.getLocalDate(2),
		rs.getOptionLocalDate(3)
	)
}

case class GetCurrentOnlineCloseResult(
	closeId: Int,
	createdOn: LocalDate,
	finalized: Option[LocalDate]
)
