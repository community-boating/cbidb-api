package org.sailcbi.APIServer.IO.PreparedQueries.Apex.DailyCloseReport

import org.sailcbi.APIServer.CbiUtil.Currency
import org.sailcbi.APIServer.IO.PreparedQueries.HardcodedQueryForSelect
import org.sailcbi.APIServer.PDFBox.Reports.DailyCloseReport.Model.APVoucherData
import org.sailcbi.APIServer.Services.Authentication.{ApexRequestCache, StaffRequestCache}
import org.sailcbi.APIServer.Services.ResultSetWrapper

class ApVouchersQuery(closeId: Int) extends HardcodedQueryForSelect[APVoucherData](Set(StaffRequestCache, ApexRequestCache)) {
	val getQuery: String =
		s"""
		   |select
		   |      p.name_first,
		   |      p.name_last,
		   |      v.value * 100,
		   |      '' as void
		   |      from persons p, ap_class_vouchers v
		   |      where p.person_Id = v.person_Id
		   |      and close_id = $closeId
		   |
       |      union all
		   |
       |      select
		   |      p.name_first,
		   |      p.name_last,
		   |      -100 * v.value,
		   |      '(VOID)' as void
		   |      from persons p, ap_class_vouchers v
		   |      where p.person_Id = v.person_Id
		   |      and void_close_id = $closeId
    """.stripMargin

	override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): APVoucherData = new APVoucherData(
		lastName = rs.getStringOrEmptyString(2),
		firstName = rs.getStringOrEmptyString(1),
		price = Currency.cents(rs.getInt(3)),
		isVoid = rs.getString(4) == "(VOID)"
	)
}