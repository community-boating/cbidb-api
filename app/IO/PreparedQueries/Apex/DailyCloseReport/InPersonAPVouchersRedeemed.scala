package IO.PreparedQueries.Apex.DailyCloseReport

import java.sql.ResultSet

import CbiUtil.Currency
import IO.PreparedQueries.HardcodedQueryForSelect
import Services.Authentication.ApexUserType

class InPersonAPVouchersRedeemed(closeId: Int) extends HardcodedQueryForSelect[InPersonAPVouchersRedeemedResult](Set(ApexUserType)) {
	val getQuery: String =
		s"""
		   |select
		   |si.close_id,
		   |nvl(sum(v.value),0)
		   |from ap_class_vouchers v, ap_class_signups si
		   |where si.close_id = $closeId
		   |and v.signup_id = si.signup_id
		   |and si.order_id is null
		   |group by si.close_id
		   |
    """.stripMargin

	override def mapResultSetRowToCaseObject(rs: ResultSet): InPersonAPVouchersRedeemedResult = new InPersonAPVouchersRedeemedResult(
		rs.getInt(1),
		Currency.dollars(rs.getDouble(2))
	)
}

class InPersonAPVouchersRedeemedResult(
											  val closeId: Int,
											  val total: Currency
									  )