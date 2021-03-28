package org.sailcbi.APIServer.IO.PreparedQueries.Apex

import com.coleji.framework.IO.PreparedQueries.HardcodedQueryForSelect
import com.coleji.framework.Storable.ResultSetWrapper
import org.sailcbi.APIServer.UserTypes.{ApexRequestCache, MemberRequestCache}

import java.time.LocalDate

class GetCurrentOnlineClose extends HardcodedQueryForSelect[GetCurrentOnlineCloseResult](Set(ApexRequestCache, MemberRequestCache)) {
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
