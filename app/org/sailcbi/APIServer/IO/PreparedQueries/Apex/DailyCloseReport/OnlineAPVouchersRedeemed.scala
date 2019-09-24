package org.sailcbi.APIServer.IO.PreparedQueries.Apex.DailyCloseReport

import java.sql.ResultSet

import org.sailcbi.APIServer.CbiUtil.Currency
import org.sailcbi.APIServer.IO.PreparedQueries.HardcodedQueryForSelect
import org.sailcbi.APIServer.Services.Authentication.ApexUserType
import org.sailcbi.APIServer.Services.ResultSetWrapper

class OnlineAPVouchersRedeemed(closeId: Int) extends HardcodedQueryForSelect[OnlineAPVouchersRedeemedResult](Set(ApexUserType)) {
	val getQuery: String =
		s"""
		   |select
		   |si.close_id,
		   |nvl(sum(v.value),0)
		   |from ap_class_vouchers v, ap_class_signups si
		   |where si.close_id = $closeId
		   |and v.signup_id = si.signup_id
		   |and si.order_id is not null
		   |group by si.close_id
		   |
    """.stripMargin

	override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): OnlineAPVouchersRedeemedResult = new OnlineAPVouchersRedeemedResult(
		rs.getInt(1),
		Currency.dollars(rs.getDouble(2))
	)
}

class OnlineAPVouchersRedeemedResult(
	val closeId: Int,
	val total: Currency
)