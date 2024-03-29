package org.sailcbi.APIServer.IO.PreparedQueries.Apex.DailyCloseReport

import com.coleji.neptune.IO.PreparedQueries.HardcodedQueryForSelect
import com.coleji.neptune.Storable.ResultSetWrapper
import org.sailcbi.APIServer.UserTypes.{ApexRequestCache, StaffRequestCache}

class CloseStaff(closeId: Int) extends HardcodedQueryForSelect[CloseStaffResult](Set(StaffRequestCache, ApexRequestCache)) {
	val getQuery: String =
		s"""
		   |select
		   |cu.close_id,
		   |cu.open_close,
		   |u.user_id,
		   |u.name_first,
		   |u.name_last
		   |from fo_close_users cu, users u
		   |where cu.close_id = $closeId
		   |and cu.user_id = u.user_id
    """.stripMargin

	override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): CloseStaffResult = new CloseStaffResult(
		rs.getInt(1),
		rs.getString(2) == "C",
		rs.getInt(3),
		rs.getStringOrEmptyString(4),
		rs.getStringOrEmptyString(5)
	)
}

class CloseStaffResult(
							  val closeId: Int,
							  val isClose: Boolean,
							  val userId: Int,
							  val firstName: String,
							  val lastName: String
					  )