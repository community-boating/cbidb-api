package org.sailcbi.APIServer.IO.PreparedQueries.Apex.DailyCloseReport

import com.coleji.framework.IO.PreparedQueries.HardcodedQueryForSelect
import org.sailcbi.APIServer.CbiUtil.{Currency, DateUtil}
import org.sailcbi.APIServer.Reports.DailyCloseReport.Model.APClassData
import org.sailcbi.APIServer.UserTypes.{ApexRequestCache, StaffRequestCache}
import org.sailcbi.APIServer.Services.ResultSetWrapper

class ApClassSignupsQuery(closeId: Int) extends HardcodedQueryForSelect[APClassData](Set(StaffRequestCache, ApexRequestCache)) {
	val getQuery: String =
		s"""
		   |select
		   |      si.signup_id,
		   |      p.name_first,
		   |      p.name_last,
		   |      t.type_name as class_name,
		   |      si.price * 100,
		   |      fs.session_datetime as start_time,
		   |      payment_medium,
		   |      cc_trans_num
		   |      from persons p, ap_class_signups si, ap_class_instances i, ap_class_formats f, ap_class_types t, ap_class_bookends bk, ap_class_sessions fs
		   |      where p.person_id = si.person_id
		   |      and si.instance_id = i.instance_id
		   |      and i.format_id = f.format_id
		   |      and f.type_id = t.type_id
		   |      and i.instance_id = bk.instance_id
		   |      and fs.session_id = bk.first_session
		   |      and si.signup_type <> 'P'
		   |      and nvl(si.price,0) > 0
		   |      and si.close_id = $closeId
		   |
       |      union all
		   |
       |      select
		   |      si.signup_id,
		   |      p.name_first,
		   |      p.name_last,
		   |      '(CANCELLED) ' ||  t.type_name as class_name,
		   |      -100 * si.price,
		   |      fs.session_datetime as start_time,
		   |      payment_medium,
		   |      cc_trans_num
		   |      from persons p, ap_class_signups si, ap_class_instances i, ap_class_formats f, ap_class_types t, ap_class_bookends bk, ap_class_sessions fs
		   |      where p.person_id = si.person_id
		   |      and si.instance_id = i.instance_id
		   |      and i.format_id = f.format_id
		   |      and f.type_id = t.type_id
		   |      and i.instance_id = bk.instance_id
		   |      and fs.session_id = bk.first_session
		   |      and si.void_close_id = $closeId
		   |      order by 2,1
		   |
    """.stripMargin

	override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): APClassData = new APClassData(
		lastName = rs.getStringOrEmptyString(3),
		firstName = rs.getStringOrEmptyString(2),
		className = rs.getStringOrEmptyString(4),
		firstSession = DateUtil.toBostonTime(rs.getLocalDateTime(6)),
		price = Currency.cents(rs.getInt(5)),
		payment = rs.getStringOrEmptyString(7)
	)
}