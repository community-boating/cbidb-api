package org.sailcbi.APIServer.IO.PreparedQueries.Apex.DailyCloseReport

import org.sailcbi.APIServer.CbiUtil.Currency
import org.sailcbi.APIServer.IO.PreparedQueries.HardcodedQueryForSelect
import org.sailcbi.APIServer.Reports.DailyCloseReport.Model.AR
import org.sailcbi.APIServer.Services.Authentication.{ApexRequestCache, StaffRequestCache}
import org.sailcbi.APIServer.Services.ResultSetWrapper

class ARQuery(closeId: Int) extends HardcodedQueryForSelect[AR](Set(StaffRequestCache, ApexRequestCache)) {
	val getQuery: String =
		s"""
		   |select
		   |      ars.source_name as source_name,
		   |      nvl(ar.value*100,0) as value
		   |      from fo_ar_sources ars, fo_ar ar
		   |      where ars.source_id = ar.source_id
		   |      and ar.close_id = $closeId
		   |      order by is_online,display_order
    """.stripMargin

	override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): AR = new AR(
		rs.getStringOrEmptyString(1),
		Currency.cents(rs.getInt(2))
	)
}