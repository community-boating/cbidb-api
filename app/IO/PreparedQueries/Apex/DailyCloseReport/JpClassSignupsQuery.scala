package IO.PreparedQueries.Apex.DailyCloseReport

import java.sql.ResultSet

import CbiUtil.{Currency, DateUtil}
import IO.PreparedQueries.HardcodedQueryForSelect
import PDFBox.Reports.DailyCloseReport.Model.JPClassData
import Services.Authentication.ApexUserType

class JpClassSignupsQuery(closeId: Int) extends HardcodedQueryForSelect[JPClassData](Set(ApexUserType)) {
  val getQuery: String =
    s"""
       |select
       |      si.signup_id,
       |      p.name_first,
       |      p.name_last,
       |      nvl(i.name_override, t.type_name) as class_name,
       |      si.price * 100,
       |      nvl(di.name_override,d.discount_name) as discount,
       |      fs.session_datetime as start_time,
       |      payment_medium,
       |      cc_trans_num
       |      from persons p, discounts d, discount_instances di, jp_class_signups si, jp_class_instances i, jp_class_types t, jp_class_bookends bk, jp_class_sessions fs
       |      where p.person_id = si.person_id
       |      and si.instance_id = i.instance_id
       |      and i.type_id = t.type_id
       |      and i.instance_id = bk.instance_id
       |      and fs.session_id = bk.first_session
       |      and si.discount_instance_id  = di.instance_id (+)
       |      and di.discount_id = d.discount_id (+)
       |      and si.close_id = $closeId
       |
       |      union all
       |
       |      select
       |      si.signup_id,
       |      p.name_first,
       |      p.name_last,
       |      '(VOID) ' || nvl(i.name_override, t.type_name) as class_name,
       |      -100 * si.price,
       |      nvl(di.name_override,d.discount_name) as discount,
       |      fs.session_datetime as start_time,
       |      payment_medium,
       |      cc_trans_num
       |      from persons p, discounts d, discount_instances di, jp_class_signups si, jp_class_instances i, jp_class_types t, jp_class_bookends bk, jp_class_sessions fs
       |      where p.person_id = si.person_id
       |      and si.instance_id = i.instance_id
       |      and i.type_id = t.type_id
       |      and i.instance_id = bk.instance_id
       |      and fs.session_id = bk.first_session
       |      and si.discount_instance_id  = di.instance_id (+)
       |      and di.discount_id = d.discount_id (+)
       |      and si.void_close_id = $closeId
       |      order by 2,1
       |
    """.stripMargin

  override def mapResultSetRowToCaseObject(rs: ResultSet): JPClassData = new JPClassData(
    lastName = rs.getString(3),
    firstName = rs.getString(2),
    className = rs.getString(4),
    firstSession = DateUtil.toBostonTime(rs.getTimestamp(7).toLocalDateTime),
    discountName = {
      val ret = rs.getString(6)
      if (rs.wasNull()) None
      else Some(ret)
    },
    price = Currency.cents(rs.getInt(5))
  )
}