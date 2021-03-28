package org.sailcbi.APIServer.IO.PreparedQueries.Apex.DailyCloseReport

import com.coleji.framework.IO.PreparedQueries.HardcodedQueryForSelect
import com.coleji.framework.Storable.ResultSetWrapper
import com.coleji.framework.Util.Currency
import org.sailcbi.APIServer.Entities.MagicIds
import org.sailcbi.APIServer.Reports.DailyCloseReport.Model.MembershipSale
import org.sailcbi.APIServer.UserTypes.{ApexRequestCache, StaffRequestCache}

class MembershipSalesQuery(closeId: Int) extends HardcodedQueryForSelect[MembershipSale](Set(StaffRequestCache, ApexRequestCache)) {
	val taxDiscrepanciesId: String = MagicIds.FO_ITEM_TAX_DISCREPANCIES.toString
	val getQuery: String =
		s"""
		   |select
		   |      nvl2(pm.order_id,'Online','In-Person') as location,
		   |      p.name_first,
		   |      p.name_last,
		   |      t.membership_type_name,
		   |      pm.price * 100,
		   |      (case when pm.discount_instance_id is not null or nvl(pm.discount_amt,0) > 0 then
		   |        nvl(di.name_override,d.discount_name)
		   |      end) as discount_name,
		   |      (case when pm.discount_instance_id is not null or nvl(pm.discount_amt,0) > 0 then
		   |        nvl(pm.discount_amt * 100,0)
		   |      end) as discount_amt
		   |      from persons p, persons_memberships pm, membership_types t, discounts d, discount_instances di
		   |      where p.person_id = pm.person_id and pm.membership_type_id = t.membership_type_id
		   |      and pm.discount_instance_id  = di.instance_id (+)
		   |      and di.discount_id = d.discount_id (+)
		   |      and pm.close_id = $closeId
		   |
       |      union all
		   |
       |      select
		   |      null,
		   |      p.name_first,
		   |      p.name_last,
		   |      '(VOID) ' || t.membership_type_name,
		   |      (-100 * pm.price),
		   |      (case when pm.discount_instance_id is not null or nvl(pm.discount_amt,0) > 0 then
		   |        nvl(di.name_override,d.discount_name)
		   |      end) as discount_name,
		   |      (case when pm.discount_instance_id is not null or nvl(pm.discount_amt,0) > 0 then
		   |        nvl(pm.discount_amt * 100,0)
		   |      end) as discount_amt
		   |      from persons p, persons_memberships pm, membership_types t, discounts d, discount_instances di
		   |      where p.person_id = pm.person_id and pm.membership_type_id = t.membership_type_id
		   |      and pm.discount_instance_id  = di.instance_id (+)
		   |      and di.discount_id = d.discount_id (+)
		   |      and pm.void_close_id = $closeId
		   |
       |      union all
		   |
       |      select
		   |      null,
		   |      null,
		   |      s.school_name,
		   |      'High School Fee',
		   |      hsf.amount * 100,
		   |      null,
		   |      null
		   |      from high_school_fees hsf, high_schools s, fo_closes c
		   |      where c.close_id = hsf.close_id and hsf.school_id = s.school_id
		   |      and c.close_id = $closeId
		   |
       |      order by 4, 6, 5, 3, 2
    """.stripMargin

	override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): MembershipSale = new MembershipSale(
		closeId,
		fullName = rs.getOptionString(2) match {
			case Some(firstName) => rs.getStringOrEmptyString(3) + ", " + firstName
			case None => rs.getStringOrEmptyString(3)
		},
		membershipType = rs.getStringOrEmptyString(4),
		discountAmount = rs.getOptionInt(7).map(Currency.cents),
		discountName = rs.getOptionString(6),
		location = rs.getOptionString(1).getOrElse("In-Person"),
		price = Currency.cents(rs.getInt(5))
	)
}