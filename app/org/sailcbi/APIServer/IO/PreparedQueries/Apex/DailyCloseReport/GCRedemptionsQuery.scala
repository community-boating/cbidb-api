package org.sailcbi.APIServer.IO.PreparedQueries.Apex.DailyCloseReport

import org.sailcbi.APIServer.CbiUtil.Currency
import org.sailcbi.APIServer.IO.PreparedQueries.HardcodedQueryForSelect
import org.sailcbi.APIServer.PDFBox.Reports.DailyCloseReport.Model.GCRedemptionData
import org.sailcbi.APIServer.Services.Authentication.ApexUserType
import org.sailcbi.APIServer.Services.ResultSetWrapper

class GCRedemptionsQuery(closeId: Int) extends HardcodedQueryForSelect[GCRedemptionData](Set(ApexUserType)) {
	val getQuery: String =
		s"""
		   |select
		   |p.name_last,
		   |p.name_first,
		   |t.membership_type_name,
		   |nvl(fagc.value*100,0) as value,
		   |gc.cert_number || ' ' || (case when gcp.purchase_price = 0 then '(Comp)' else '(Paid)' end) as cert_number
		   |from fo_applied_gc fagc, persons_memberships pm, persons p, membership_types t, gift_certificates gc, gift_cert_purchases gcp
		   |where fagc.pm_assign_id = pm.assign_id
		   |and fagc.cert_id = gcp.cert_id
		   |and pm.person_id = p.person_id
		   |and pm.membership_type_id = t.membership_type_id
		   |and fagc.cert_id = gc.cert_id
		   |and fagc.close_id = $closeId
		   |
       |union all
		   |
       |select
		   |p.name_last,
		   |p.name_first,
		   |'Online',
		   |nvl(fagc.value*100,0),
		   |gc.cert_number || ' ' || (case when gcp.purchase_price = 0 then '(Comp)' else '(Paid)' end) as cert_number
		   |from fo_applied_gc fagc, order_numbers o, persons p, gift_certificates gc, gift_cert_purchases gcp
		   |where fagc.order_id = o.order_id and o.person_id = p.person_id
		   |and fagc.cert_id = gcp.cert_id
		   |and fagc.cert_id = gc.cert_id
		   |and fagc.pm_assign_id is null
		   |and fagc.close_id = $closeId
		   |
       |union all
		   |
       |select
		   |null,
		   |null,
		   |'Misc',
		   |nvl(fagc.value*100,0),
		   |gc.cert_number
		   |from fo_applied_gc fagc, gift_certificates gc, gift_cert_purchases gcp
		   |where fagc.cert_id = gc.cert_id
		   |and fagc.cert_id = gcp.cert_id
		   |and fagc.pm_assign_Id is null
		   |and fagc.order_id is null
		   |and fagc.close_id = $closeId
    """.stripMargin

	override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): GCRedemptionData = new GCRedemptionData(
		lastName = rs.getStringOrEmptyString(1),
		firstName = rs.getStringOrEmptyString(2),
		certNumber = rs.getStringOrEmptyString(5),
		usedFor = rs.getStringOrEmptyString(3),
		amount = Currency.cents(rs.getInt(4))
	)
}