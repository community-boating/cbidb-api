package org.sailcbi.APIServer.IO.PreparedQueries.Apex.DailyCloseReport

import com.coleji.framework.IO.PreparedQueries.HardcodedQueryForSelect
import org.sailcbi.APIServer.CbiUtil.Currency
import org.sailcbi.APIServer.Reports.DailyCloseReport.Model.RetailData
import org.sailcbi.APIServer.UserTypes.{ApexRequestCache, StaffRequestCache}
import org.sailcbi.APIServer.Services.ResultSetWrapper

class RetailQuery(closeId: Int) extends HardcodedQueryForSelect[RetailData](Set(StaffRequestCache, ApexRequestCache)) {
	val getQuery: String =
		s"""
		   |select
		   |      il.item_name||' ('||to_char(t.unit_price,'FM$$999G999G999G999G990D00')||')' as item_name,
		   |      sum(t.qty_sold) as qty_sold,
		   |      100 * sum((t.unit_price * t.qty_sold) - t.discounts_total) as pretax,
		   |      sum(t.discounts_total * 100) as discounts_total,
		   |	  round(100 * sum(nvl(t.tax_rate,0) * ((t.unit_price * t.qty_sold) - t.discounts_total))) as tax_amount,
		   |	  round(100 * sum((1+nvl(t.tax_rate,0)) * ((t.unit_price * t.qty_sold) - t.discounts_total))) as total,
		   |      sum(t.qty_comped) as qty_comped,
		   |      100 * sum((t.unit_price * t.qty_comped)) as value_comped
		   |      from fo_item_types t, fo_item_types_lookup tl, fo_items_lookup il
		   |      where t.type_id = tl.type_id and tl.item_id = il.item_id
		   |      and t.close_id = $closeId
		   |      and il.category_id = 1
		   |      and il.active = 'Y' and tl.active = 'Y'
		   |      group by il.item_name||' ('||to_char(t.unit_price,'FM$$999G999G999G999G990D00')||')'
		   |
       |      union all
		   |
       |
       |      select
		   |      il.item_name||' ('||to_char(i.unit_price,'FM$$999G999G999G999G990D00')||')' as item_name,
		   |      i.qty_sold,
		   |      100 * ((i.unit_price * i.qty_sold) - i.discounts_total) as pretax,
		   |      i.discounts_total * 100,
		   |	  round(100 * (nvl(i.tax_rate,0) * ((i.unit_price * i.qty_sold) - i.discounts_total))) as tax_amount,
		   |	  round(100 * ((1+nvl(i.tax_rate,0)) * ((i.unit_price * i.qty_sold) - i.discounts_total))) as total,
		   |      i.qty_comped,
		   |      100 * (i.unit_price * i.qty_comped) as value_comped
		   |      from fo_items i, fo_items_lookup il
		   |      where i.item_id = il.item_id
		   |      and i.close_id = $closeId
		   |      and il.category_id = 1
		   |      and not exists (select 1 from fo_item_types_lookup where item_id = il.item_id)
		   |
       |
       |      union all
		   |
       |
       |      select
		   |      il.item_name||' ('||to_char(i.unit_price,'FM$$999G999G999G999G990D00')||')' as item_name,
		   |      i.qty_sold,
		   |      100 * ((i.unit_price * i.qty_sold) - i.discounts_total) as pretax,
		   |      i.discounts_total * 100,
		   |      round(100 * (nvl(i.tax_rate,0) * ((i.unit_price * i.qty_sold) - i.discounts_total))) as tax_amount,
		   |      round(100 * (1+nvl(i.tax_rate,0)) * ((i.unit_price * i.qty_sold) - i.discounts_total)) as total,
		   |      i.qty_comped,
		   |      100 * (i.unit_price * i.qty_comped) as value_comped
		   |      from fo_items_lookup il, fo_items i
		   |      where i.item_id  = il.item_id
		   |      and i.close_id  = $closeId
		   |      and category_id = 2
		   |
       |
       |      union all
		   |
       |
       |      select
		   |      ml.item_name,
		   |      m.qty_sold,
		   |	  100 * m.total_pretax,
		   |	  0,
		   |	  100 * m.total_pretax * m.tax_rate,
		   |      100 * m.total_price as value_sold,
		   |      m.qty_comped,
		   |      100 * m.total_comp as value_comped
		   |      from fo_misc_lookup ml, fo_misc m
		   |      where ml.item_id = m.item_id
		   |      and m.close_id = $closeId
		   |
       |      order by 1
    """.stripMargin

	override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): RetailData = new RetailData(
		itemName = rs.getStringOrEmptyString(1),
		numberSold = rs.getInt(2),
		discounts = Currency.cents(rs.getInt(4)),
		pretax = Currency.cents(rs.getInt(3)),
		taxAmount = Currency.cents(rs.getInt(5)),
		totalAmount = Currency.cents(rs.getInt(6)),
		numComp = rs.getInt(7),
		amountComp = Currency.cents(rs.getInt(8))
	)
}