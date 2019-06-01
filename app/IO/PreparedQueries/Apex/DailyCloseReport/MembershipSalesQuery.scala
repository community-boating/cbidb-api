package IO.PreparedQueries.Apex.DailyCloseReport

import java.sql.ResultSet

import CbiUtil.Currency
import Entities.MagicIds
import IO.PreparedQueries.HardcodedQueryForSelect
import PDFBox.Reports.DailyCloseReport.Model.MembershipSale
import Services.Authentication.ApexUserType

class MembershipSalesQuery(closeId: Int) extends HardcodedQueryForSelect[MembershipSale](Set(ApexUserType)) {
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

	override def mapResultSetRowToCaseObject(rs: ResultSet): MembershipSale = new MembershipSale(
		closeId,
		fullName = {
			val lastname = rs.getStringOrEmptyString(3)
			val firstName = rs.getStringOrEmptyString(2)
			if (rs.wasNull()) lastname
			else lastname + ", " + firstName
		},
		membershipType = rs.getStringOrEmptyString(4),
		discountAmount = {
			val ret = rs.getInt(7)
			if (rs.wasNull()) None
			else Some(Currency.cents(ret))
		},
		discountName = {
			val ret = rs.getString(6)
			if (rs.wasNull()) None
			else Some(ret)
		},
		location = {
			val ret = rs.getString(1)
			if (rs.wasNull()) "In-Person"
			else ret
		},
		price = Currency.cents(rs.getInt(5))
	)
}