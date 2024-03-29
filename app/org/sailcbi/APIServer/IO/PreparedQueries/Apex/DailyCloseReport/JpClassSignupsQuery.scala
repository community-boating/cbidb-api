package org.sailcbi.APIServer.IO.PreparedQueries.Apex.DailyCloseReport

import com.coleji.neptune.IO.PreparedQueries.HardcodedQueryForSelect
import com.coleji.neptune.Storable.ResultSetWrapper
import com.coleji.neptune.Util.{Currency, DateUtil}
import org.sailcbi.APIServer.Reports.DailyCloseReport.Model.JPClassData
import org.sailcbi.APIServer.UserTypes.{ApexRequestCache, StaffRequestCache}

class JpClassSignupsQuery(closeId: Int) extends HardcodedQueryForSelect[JPClassData](Set(StaffRequestCache, ApexRequestCache)) {
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

	override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): JPClassData = new JPClassData(
		lastName = rs.getStringOrEmptyString(3),
		firstName = rs.getStringOrEmptyString(2),
		className = rs.getStringOrEmptyString(4),
		firstSession = DateUtil.toBostonTime(rs.getLocalDateTime(7)),
		discountName = rs.getOptionString(6),
		price = Currency.cents(rs.getInt(5))
	)
}