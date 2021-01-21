package org.sailcbi.APIServer.IO.PreparedQueries.Apex.DailyCloseReport

import org.sailcbi.APIServer.CbiUtil.Currency
import org.sailcbi.APIServer.IO.PreparedQueries.HardcodedQueryForSelect
import org.sailcbi.APIServer.PDFBox.Reports.DailyCloseReport.Model.SummaryItem
import org.sailcbi.APIServer.Services.Authentication.{ApexUserType, StaffUserType}
import org.sailcbi.APIServer.Services.ResultSetWrapper

class SummaryQuery(closeId: Int) extends HardcodedQueryForSelect[SummaryItem](Set(StaffUserType, ApexUserType)) {
	val getQuery: String =
		s"""
		   |select distinct
		   |      base.item,
		   |      base.discount,
		   |      ip.count as ip_count,
		   |	    100 * ip.total_price as ip_total,
		   |     null,
		   |      ol.count as ol_count,
		   |      100 * ol.total_price as ol_total
		   |      from close_summary base
		   |      left outer join close_summary ip
		   |        on base.item = ip.item and nvl(base.discount,'$$') = nvl(ip.discount,'$$')
		   |        and base.close_id = ip.close_id and nvl(ip.location,'In-Person') = 'In-Person'
		   |        and ip.is_retail is null
		   |      left outer join close_summary ol
		   |        on base.item = ol.item and nvl(base.discount,'$$') = nvl(ol.discount,'$$')
		   |        and base.close_id = ol.close_id and ol.location = 'Online'
		   |        and ol.is_retail is null
		   |      where base.close_id = $closeId
		   |      and base.is_retail is null
		   |
       |      union all
		   |
       |    select
		   |    '** Total Retail (untaxed) **',
		   |    null,
		   |    sum(count),
		   |	100 * sum(total_price) as ip_total,
		   | null,
		   |    null,
		   |    null
		   |    from close_summary
		   |    where close_Id = $closeId
		   |    and is_retail = 'Y'
		   |	and nvl(tax_amount,0) = 0
		   |    having sum(count) > 0
		   |
       |
       |	union all
		   |
       |	    select
		   |	    '** Total Retail (taxable) **',
		   |	    null,
		   |	    sum(count),
		   |     sum(nvl(100 * total_pretax,0)) as ip_total,
		   |     sum(nvl(round(100 * tax_amount),0)) as tax_amt,
		   |	    null,
		   |	    null
		   |	    from close_summary
		   |	    where close_Id = $closeId
		   |	    and is_retail = 'Y'
		   |		and nvl(tax_amount,0) > 0
		   |	    having sum(count) > 0
		   |
       |
       |
       |    order by 1,2 nulls first
    """.stripMargin

	override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): SummaryItem = new SummaryItem(
		itemName = rs.getStringOrEmptyString(1),
		discountName = rs.getOptionString(2),
		inpersonCount = rs.getOptionInt(3),
		inpersonTotal = rs.getOptionInt(4).map(ip => rs.getOptionInt(5) match {
			case Some(tax) => Currency.cents(ip).format() + " + " + Currency.cents(tax).format()
			case None => Currency.cents(ip).format()
		}),
		onlineCount = rs.getOptionInt(6),
		onlineTotal = rs.getOptionInt(7).map(Currency.cents)
	)
}