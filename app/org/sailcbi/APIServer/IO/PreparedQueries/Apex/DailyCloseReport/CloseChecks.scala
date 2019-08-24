package org.sailcbi.APIServer.IO.PreparedQueries.Apex.DailyCloseReport

import java.sql.ResultSet

import org.sailcbi.APIServer.CbiUtil.Currency
import org.sailcbi.APIServer.IO.PreparedQueries.HardcodedQueryForSelect
import org.sailcbi.APIServer.PDFBox.Reports.DailyCloseReport.Model.Check
import org.sailcbi.APIServer.Services.Authentication.ApexUserType

class CloseChecks(closeId: Int) extends HardcodedQueryForSelect[Check](Set(ApexUserType)) {
	val getQuery: String =
		s"""
		   |select check_num, value * 100,
		   |  (select substr(listagg(', '||hs.school_name) within group (order by hs.display_order),3) from high_schools hs, high_school_payments hsp
		   |    where hs.school_id = hsp.school_id and hsp.check_id = c.check_id) as school_list
		   |  from fo_checks c where close_id = $closeId
    """.stripMargin

	override def mapResultSetRowToCaseObject(rs: ResultSet): Check = new Check(
		{
			val ret = rs.getString(1)
			if (rs.wasNull()) None
			else Some(ret)
		},
		Currency.cents(rs.getInt(2)),
		{
			val ret = rs.getString(3)
			if (rs.wasNull()) None
			else Some(ret)
		}
	)
}