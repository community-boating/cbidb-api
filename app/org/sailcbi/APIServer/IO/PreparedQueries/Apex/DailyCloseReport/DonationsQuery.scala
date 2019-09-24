package org.sailcbi.APIServer.IO.PreparedQueries.Apex.DailyCloseReport

import java.sql.ResultSet

import org.sailcbi.APIServer.CbiUtil.{Currency, DateUtil}
import org.sailcbi.APIServer.IO.PreparedQueries.HardcodedQueryForSelect
import org.sailcbi.APIServer.PDFBox.Reports.DailyCloseReport.Model.Donation
import org.sailcbi.APIServer.Services.Authentication.ApexUserType
import org.sailcbi.APIServer.Services.ResultSetWrapper

class DonationsQuery(closeId: Int) extends HardcodedQueryForSelect[Donation](Set(ApexUserType)) {
	val getQuery: String =
		s"""
		   |select
		   |      nvl2(d.order_id,'Online','In-Person') as location,
		   |      nvl2(p.name_first||p.name_last,p.name_last||', '||p.name_first,p.organization) as name,
		   |      d.amount * 100,
		   |      df.fund_name,
		   |      d.donation_date as donation_date
		   |      from donations d, persons p, donation_funds df, donation_initiatives di
		   |      where d.person_id = p.person_id
		   |      and d.fund_id = df.fund_id
		   |      and d.initiative_id = di.initiative_id
		   |      and d.close_id = $closeId
		   |
       |      union all
		   |
       |      select
		   |      null,
		   |      nvl2(p.name_first||p.name_last,p.name_last||', '||p.name_first,p.organization) as name,
		   |      -100 * d.amount,
		   |      df.fund_name,
		   |      d.donation_date as donation_date
		   |      from donations d, persons p, donation_funds df, donation_initiatives di
		   |      where d.person_id = p.person_id
		   |      and d.fund_id = df.fund_id
		   |      and d.initiative_id = di.initiative_id
		   |      and d.void_close_id = $closeId
		   |
       |
       |      order by 2,1
    """.stripMargin

	override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): Donation = new Donation(
		name = rs.getStringOrEmptyString(2),
		fundName = rs.getStringOrEmptyString(4),
		donationDate = DateUtil.toBostonTime(rs.getLocalDateTime(5)),
		location = rs.getStringOrEmptyString(1),
		amount = Currency.cents(rs.getInt(3))
	)
}