package IO.PreparedQueries.Apex.DailyCloseReport

import java.sql.ResultSet

import CbiUtil.Currency
import Entities.MagicIds
import IO.PreparedQueries.HardcodedQueryForSelect
import PDFBox.Reports.DailyCloseReport.Model.TaxablesItem
import Services.Authentication.ApexUserType

class TaxablesQuery(closeId: Int) extends HardcodedQueryForSelect[TaxablesItem](Set(ApexUserType)) {
	val taxDiscrepanciesId: String = MagicIds.FO_ITEM_TAX_DISCREPANCIES.toString
	val getQuery: String =
		s"""
		   |select
		   |        item,
		   |        discount,
		   |        count,
		   |        total_pretax * 100,
		   |        round(tax_amount * 100),
		   |        round(total_price * 100)
		   |        from close_summary where close_id = $closeId and nvl(tax_amount,0) > 0
		   |
       |		union all
		   |		select
		   |		ml.item_name,
		   |		null,
		   |		1,
		   |		0,
		   |		m.total_price * 100,
		   |		m.total_price * 100
		   |		from fo_misc_lookup ml, fo_misc m
		   |		where ml.item_id = m.item_id
		   |		and m.close_id = $closeId
		   |		and m.item_id = $taxDiscrepanciesId
		   |
    """.stripMargin

	override def mapResultSetRowToCaseObject(rs: ResultSet): TaxablesItem = new TaxablesItem(
		closeId,
		rs.getStringOrEmptyString(1),
		{
			val ret = rs.getString(2)
			if (rs.wasNull()) None
			else Some(ret)
		},
		rs.getInt(3),
		Currency.cents(rs.getInt(4)),
		Currency.cents(rs.getInt(5))
	)
}