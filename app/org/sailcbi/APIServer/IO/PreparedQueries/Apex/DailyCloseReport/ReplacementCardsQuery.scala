package org.sailcbi.APIServer.IO.PreparedQueries.Apex.DailyCloseReport

import com.coleji.framework.IO.PreparedQueries.HardcodedQueryForSelect
import org.sailcbi.APIServer.CbiUtil.Currency
import org.sailcbi.APIServer.Reports.DailyCloseReport.Model.ReplacementCardData
import org.sailcbi.APIServer.Services.ResultSetWrapper
import org.sailcbi.APIServer.UserTypes.{ApexRequestCache, StaffRequestCache}

class ReplacementCardsQuery(closeId: Int) extends HardcodedQueryForSelect[ReplacementCardData](Set(StaffRequestCache, ApexRequestCache)) {
	val getQuery: String =
		s"""
		   |select
		   |      p.name_first,
		   |      p.name_last,
		   |      pc.price * 100
		   |      from persons p, persons_cards pc
		   |      where pc.person_id = p.person_id
		   |      and pc.close_id = $closeId
		   |      and pc.price is not null
		   |      order by 2,1
    """.stripMargin

	override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): ReplacementCardData = new ReplacementCardData(
		fullName = rs.getStringOrEmptyString(2) + ", " + rs.getStringOrEmptyString(1),
		price = Currency.cents(rs.getInt(3))
	)
}