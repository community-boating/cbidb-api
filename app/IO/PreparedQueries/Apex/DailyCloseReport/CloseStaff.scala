package IO.PreparedQueries.Apex.DailyCloseReport

import java.sql.ResultSet

import IO.PreparedQueries.HardcodedQueryForSelect
import Services.Authentication.ApexUserType

class CloseStaff(closeId: Int) extends HardcodedQueryForSelect[CloseStaffResult](Set(ApexUserType)) {
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

	override def mapResultSetRowToCaseObject(rs: ResultSet): CloseStaffResult = new CloseStaffResult(
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