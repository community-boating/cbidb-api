package org.sailcbi.APIServer.IO.PreparedQueries.Apex.DailyCloseReport

import com.coleji.framework.IO.PreparedQueries.HardcodedQueryForSelect
import com.coleji.framework.Storable.ResultSetWrapper
import com.coleji.framework.Util.Currency
import org.sailcbi.APIServer.Reports.DailyCloseReport.Model.GCSalesData
import org.sailcbi.APIServer.UserTypes.{ApexRequestCache, StaffRequestCache}

class GCSalesQuery(closeId: Int) extends HardcodedQueryForSelect[GCSalesData](Set(StaffRequestCache, ApexRequestCache)) {
	val getQuery: String =
		s"""
		   |select
		   |nvl2(p.person_id,p.name_last||', '||p.name_first,'COMP''D BY CBI') as purchaser,
		   |recipient_name_last||', '||recipient_name_first as recipient,
		   |nvl(''||gc.cert_number,'(Not Processed)') as cert_number,
		   |t.membership_type_name,
		   |100 * gcp.purchase_price,
		   |100 * gcp.purchase_value,
		   |(case when gcp.purchase_value <> gcp.purchase_price then gcp.purchase_value - gcp.purchase_price end) as discount_amt
		   |from gift_cert_purchases gcp, membership_types t, persons p, gift_certificates gc
		   |where gcp.cert_id = gc.cert_id (+)
		   |and gcp.purchase_mem_type_id = t.membership_type_id (+)
		   |and gcp.purchaser_id = p.person_id (+)
		   |and gcp.purchase_close_id = $closeId
		   |and gcp.purchaser_id <> 1
		   |
       |union all
		   |select
		   |nvl2(p.person_id,p.name_last||', '||p.name_first,'COMP''D BY CBI') as purchaser,
		   |recipient_name_last||', '||recipient_name_first as recipient,
		   |nvl('(VOID) '||gc.cert_number,'(VOID Not Processed)') as cert_number,
		   |t.membership_type_name,
		   |-100 * gcp.purchase_price,
		   |100 * gcp.purchase_value,
		   |(case when gcp.purchase_value <> gcp.purchase_price then gcp.purchase_value - gcp.purchase_price end) as discount_amt
		   |from gift_cert_purchases gcp, membership_types t, persons p, gift_certificates gc
		   |where gcp.cert_id = gc.cert_id (+)
		   |and gcp.purchase_mem_type_id = t.membership_type_id (+)
		   |and gcp.purchaser_id = p.person_id (+)
		   |and gcp.void_close_id = $closeId
		   |and gcp.purchaser_id <> 1
		   |order by 1,2
    """.stripMargin

	override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): GCSalesData = new GCSalesData(
		purchaser = rs.getStringOrEmptyString(1),
		recipient = rs.getStringOrEmptyString(2),
		certNumber = rs.getStringOrEmptyString(3),
		paid = Currency.cents(rs.getInt(5)),
		value = rs.getOptionInt(7).map(i => Currency.cents(rs.getInt(6)).format() + " (Discount amt: " + Currency.cents(i).format() + ")")
	)
}