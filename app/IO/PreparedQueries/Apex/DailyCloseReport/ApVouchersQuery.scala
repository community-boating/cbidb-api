package IO.PreparedQueries.Apex.DailyCloseReport

import java.sql.ResultSet

import CbiUtil.Currency
import IO.PreparedQueries.HardcodedQueryForSelect
import PDFBox.Reports.DailyCloseReport.Model.APVoucherData
import Services.Authentication.ApexUserType

class ApVouchersQuery(closeId: Int) extends HardcodedQueryForSelect[APVoucherData](Set(ApexUserType)) {
  val getQuery: String =
    s"""
       |select
       |      p.name_first,
       |      p.name_last,
       |      v.value * 100,
       |      '' as void
       |      from persons p, ap_class_vouchers v
       |      where p.person_Id = v.person_Id
       |      and close_id = $closeId
       |
       |      union all
       |
       |      select
       |      p.name_first,
       |      p.name_last,
       |      -100 * v.value,
       |      '(VOID)' as void
       |      from persons p, ap_class_vouchers v
       |      where p.person_Id = v.person_Id
       |      and void_close_id = $closeId
    """.stripMargin

  override def mapResultSetRowToCaseObject(rs: ResultSet): APVoucherData = new APVoucherData(
    lastName = rs.getString(2),
    firstName = rs.getString(1),
    price = Currency.cents(rs.getInt(3)),
    isVoid = rs.getString(4) == "(VOID)"
  )
}