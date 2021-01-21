package org.sailcbi.APIServer.IO.PreparedQueries.Apex.DailyCloseReport

import org.sailcbi.APIServer.CbiUtil.Currency
import org.sailcbi.APIServer.IO.PreparedQueries.HardcodedQueryForSelect
import org.sailcbi.APIServer.PDFBox.Reports.DailyCloseReport.Model.Check
import org.sailcbi.APIServer.Services.Authentication.{ApexUserType, StaffUserType}
import org.sailcbi.APIServer.Services.ResultSetWrapper

class CloseChecks(closeId: Int) extends HardcodedQueryForSelect[Check](Set(StaffUserType, ApexUserType)) {
	val getQuery: String =
		s"""
		   |select check_num, check_name, check_date, value * 100,
		   |  (select substr(listagg(', '||hs.school_name) within group (order by hs.display_order),3) from high_schools hs, high_school_payments hsp
		   |    where hs.school_id = hsp.school_id and hsp.check_id = c.check_id) as school_list
		   |  from fo_checks c where close_id = $closeId
    """.stripMargin

	override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): Check = new Check(
		checkNumber = rs.getOptionString(1),
		value = Currency.cents(rs.getInt(4)),
		school = rs.getOptionString(5),
		checkName = rs.getOptionString(2),
		checkDate = rs.getOptionLocalDate(3)
	)
}