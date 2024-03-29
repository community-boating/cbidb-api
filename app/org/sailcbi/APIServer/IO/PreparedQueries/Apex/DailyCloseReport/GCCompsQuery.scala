package org.sailcbi.APIServer.IO.PreparedQueries.Apex.DailyCloseReport

import com.coleji.neptune.IO.PreparedQueries.HardcodedQueryForSelect
import com.coleji.neptune.Storable.ResultSetWrapper
import com.coleji.neptune.Util.Currency
import org.sailcbi.APIServer.Reports.DailyCloseReport.Model.GCCompsData
import org.sailcbi.APIServer.UserTypes.{ApexRequestCache, StaffRequestCache}

class GCCompsQuery(closeId: Int) extends HardcodedQueryForSelect[GCCompsData](Set(StaffRequestCache, ApexRequestCache)) {
	val getQuery: String =
		s"""
		   |select
		   |    recipient_name_last||', '||recipient_name_first as recipient,
		   |    nvl(''||gc.cert_number,'(Not Processed)') as cert_number,
		   |    t.membership_type_name,
		   |    gcp.purchase_price * 100,
		   |    gcp.purchase_value * 100
		   |	from gift_cert_purchases gcp, membership_types t, persons p, gift_certificates gc
		   |    where gcp.cert_id = gc.cert_id (+)
		   |    and gcp.purchase_mem_type_id = t.membership_type_id (+)
		   |    and gcp.purchaser_id = p.person_id (+)
		   |    and gcp.purchase_close_id = $closeId
		   |	and gcp.purchaser_id = 1
		   |    order by 1,2
    """.stripMargin

	override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): GCCompsData = new GCCompsData(
		recipient = rs.getStringOrEmptyString(1),
		certNumber = rs.getStringOrEmptyString(2),
		value = Currency.cents(rs.getInt(5))
	)
}