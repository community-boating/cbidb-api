package IO.PreparedQueries.Apex.DailyCloseReport

import java.sql.ResultSet

import CbiUtil.Currency
import IO.PreparedQueries.HardcodedQueryForSelect
import PDFBox.Reports.DailyCloseReport.Model.GCSalesData
import Services.Authentication.ApexUserType

class GCSalesQuery(closeId: Int) extends HardcodedQueryForSelect[GCSalesData](Set(ApexUserType)) {
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

  override def mapResultSetRowToCaseObject(rs: ResultSet): GCSalesData = new GCSalesData(
    purchaser = rs.getString(1),
    recipient = rs.getString(2),
    certNumber = rs.getString(3),
    paid = Currency.cents(rs.getInt(5)),
    value = {
      val discount = rs.getInt(7)
      if (rs.wasNull()) None
      else Some(Currency.cents(rs.getInt(6)).format() + " (Discount amt: " + Currency.cents(discount).format() + ")")
    }
  )
}