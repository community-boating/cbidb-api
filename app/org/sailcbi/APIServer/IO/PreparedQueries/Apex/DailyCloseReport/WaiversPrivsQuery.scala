package org.sailcbi.APIServer.IO.PreparedQueries.Apex.DailyCloseReport

import com.coleji.framework.IO.PreparedQueries.HardcodedQueryForSelect
import org.sailcbi.APIServer.CbiUtil.Currency
import org.sailcbi.APIServer.Reports.DailyCloseReport.Model.WaiverPrivData
import org.sailcbi.APIServer.UserTypes.{ApexRequestCache, StaffRequestCache}
import org.sailcbi.APIServer.Services.ResultSetWrapper

class WaiversPrivsQuery(closeId: Int) extends HardcodedQueryForSelect[WaiverPrivData](Set(StaffRequestCache, ApexRequestCache)) {
	val getQuery: String =
		s"""
		   |select
		   |      'WAIVER' as identifier,
		   |      p.name_last||', '||p.name_first as full_name,
		   |      w.price * 100,
		   |      nvl2(w.order_id,'Online','In-Person') as ipol
		   |      from persons p, damage_waivers w
		   |      where p.person_id = w.person_id
		   |      and w.close_id = $closeId
		   |
       |      union all
		   |
       |      select
		   |      'VOID WAIVER' as identifier,
		   |      p.name_last||', '||p.name_first as full_name,
		   |      (-100 * w.price),
		   |      null
		   |      from persons p, damage_waivers w
		   |      where p.person_id = w.person_id
		   |      and w.void_close_id = $closeId
		   |
       |      union all
		   |
       |      select
		   |      'GUEST',
		   |      p.name_last||', '||p.name_first,
		   |      g.price * 100,
		   |      nvl2(g.order_id,'Online','In-Person') as ipol
		   |      from persons p, persons_memberships pm, guest_privs g
		   |      where p.person_id = pm.person_id
		   |      and pm.assign_id = g.membership_id
		   |      and g.close_id = $closeId
		   |
       |      union all
		   |
       |      select
		   |      'VOID GUEST',
		   |      p.name_last||', '||p.name_first,
		   |      (-100 * g.price),
		   |      null
		   |      from persons p, persons_memberships pm, guest_privs g
		   |      where p.person_id = pm.person_id
		   |      and pm.assign_id = g.membership_id
		   |      and g.void_close_id = $closeId
		   |      order by 1,2
    """.stripMargin

	override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): WaiverPrivData = new WaiverPrivData(
		identifier = rs.getStringOrEmptyString(1),
		location = rs.getOptionString(4).getOrElse(""),
		fullName = rs.getStringOrEmptyString(2),
		price = Currency.cents(rs.getInt(3))
	)
}