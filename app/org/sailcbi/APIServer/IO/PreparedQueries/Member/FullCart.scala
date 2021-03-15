package org.sailcbi.APIServer.IO.PreparedQueries.Member

import org.sailcbi.APIServer.IO.PreparedQueries.PreparedQueryForSelect
import org.sailcbi.APIServer.Services.Authentication.{MemberRequestCache, ProtoPersonRequestCache}
import org.sailcbi.APIServer.Services.ResultSetWrapper
import play.api.libs.json.Json

class FullCart(orderId: Int) extends PreparedQueryForSelect[FullCartItemResult](allowedUserTypes = Set(MemberRequestCache, ProtoPersonRequestCache)) {
	override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): FullCartItemResult =
		FullCartItemResult(
			itemNameHTML = rsw.getString(1),
			itemId = rsw.getDouble(2),
			itemType = rsw.getString(3),
			nameFirst = rsw.getOptionString(4),
			nameLast = rsw.getOptionString(5),
			price = rsw.getDouble(6),
			displayOrder = rsw.getDouble(7),
			orderId = rsw.getInt(8),
			fundId = rsw.getOptionInt(9),
			inMemoryOf = rsw.getOptionString(10),
		)

	override def getQuery: String =
		"""
		  |select
		  |nvl(m.membership_type_display_name,m.membership_type_name) as item_name,
		  |c.item_id,
		  |'Membership' as item_type,
		  |p.name_first,
		  |p.name_last,
		  |c.price as price,
		  |2 as display_order,
		  |c.order_id,
		  |null,
		  |null
		  |from shopping_cart_memberships c, membership_types m, persons p
		  |where c.person_id = p.person_id
		  |and c.membership_type_id = m.membership_type_id
		  |and c.ready_to_buy <> 'N'
		  |and c.order_id = ?
		  |
		  |union all
		  |
		  |select
		  |'Junior Class - '||nvl(i.name_override,t.type_name),
		  |scj.item_id,
		  |'Class',
		  |p.name_first,
		  |p.name_last,
		  |scj.price,
		  |3,
		  |scj.order_id,
		  |null,
		  |null
		  |from jp_class_types t, jp_class_instances i, shopping_cart_jpc scj, persons p
		  |where t.type_id = i.type_id and i.instance_id = scj.instance_id and scj.person_id = p.person_id
		  |and scj.ready_to_buy <> 'N'
		  |and scj.order_id = ?
		  |
		  |union all
		  |
		  |select
		  |'Donation - '||f.fund_name,
		  |d.item_id,
		  |'Donation' as item_type,
		  |null,
		  |null,
		  |amount,
		  |1,
		  |d.order_id,
		  |d.fund_id,
		  |d.in_memory_of
		  |from shopping_cart_donations d, donation_funds f
		  |where d.fund_id = f.fund_id
		  |and d.order_id = ?
		  |
		  |
		  |union all
		  |
		  |select
		  |'Damage Waiver',
		  |w.item_id,
		  |'Damage Waiver',
		  |null,
		  |null,
		  |price,
		  |4,
		  |w.order_id,
		  |null,
		  |null
		  |from shopping_cart_waivers w
		  |where w.order_id = ?
		  |
		  |union all
		  |
		  |select
		  |'<i>Discount - '||nvl(di.name_override,d.discount_name)||'</i>',
		  |scm.item_id+0.1,
		  |'Discount',
		  |null,
		  |null,
		  |(-1)*scm.discount_amt,
		  |2,
		  |scm.order_id,
		  |null,
		  |null
		  |from shopping_cart_memberships scm, discounts d, discount_instances di
		  |where scm.discount_instance_id = di.instance_id and di.discount_id = d.discount_id
		  |and scm.discount_instance_id is not null
		  |and scm.discount_amt is not null
		  |and scm.order_id = ?
		  |
		  |union all
		  |
		  |select
		  |'<i>Discount - '||nvl(di.name_override,d.discount_name)||'</i>',
		  |scj.item_id+0.1,
		  |'Discount',
		  |null,
		  |null,
		  |(-1)*scj.discount_amt,
		  |3,
		  |scj.order_id,
		  |null,
		  |null
		  |from shopping_cart_jpc scj, discounts d, discount_instances di
		  |where scj.discount_instance_id = di.instance_id and di.discount_id = d.discount_id
		  |and scj.discount_instance_id is not null
		  |and scj.discount_amt is not null
		  |and scj.order_id = ?
		  |
		  |union all
		  |select
		  |'Gift Certificate - '||(case when membership_type_id is not null then (select membership_type_name from membership_types where membership_type_id = scgc.membership_type_id) else 'Fixed Amount' end),
		  |scgc.item_id,
		  |'Gift Certificate',
		  |recipient_name_first,
		  |recipient_name_last,
		  |purchase_price,
		  |5,
		  |order_id,
		  |null,
		  |null
		  |from shopping_cart_gcs scgc
		  |where order_id = ?
		  |
		  |
		  |union all
		  |
		  |select
		  |'<i>Gift Certificate Redeemed</i>',
		  |cert_id,
		  |'Gift Certificate Redeemed',
		  |null,
		  |null,
		  |-1 * amount,
		  |6,
		  |order_id,
		  |null,
		  |null
		  |from shopping_cart_appl_gc
		  |where order_id is not null
		  |and order_id = ?
		  |
		  |union all
		  |
		  |
		  |select
		  |'Guest Privileges',
		  |scg.item_id,
		  |'Guest Privileges',
		  |null,
		  |null,
		  |scg.price,
		  |4.5,
		  |scg.order_id,
		  |null,
		  |null
		  |from shopping_cart_guest_privs scg
		  |where scg.order_id = ?
		  |
		  |union all
		  |
		  |select
		  |'AP Class - '||t.type_name||' ('||trim(leading '0' from to_char(fs.session_datetime,'MM'))||'/'||
		  |trim(leading '0' from to_char(fs.session_datetime,'DD'))||' '||trim(leading '0' from to_char(fs.session_datetime,'HH:MIPM'))||')',
		  |si.signup_id,
		  |'Class',
		  |null,
		  |null,
		  |si.price,
		  |7,
		  |si.order_id,
		  |null,
		  |null
		  |from ap_class_signups si, ap_class_bookends bk, ap_class_sessions fs, ap_class_instances i, ap_class_formats f, ap_class_types t
		  |where si.instance_id= bk.instance_id and bk.first_session = fs.session_id
		  |and si.instance_id = i.instance_id and i.format_id = f.format_id and f.type_id = t.type_id
		  |and si.signup_type in ('P','E') and nvl(si.price,0) > 0 and not exists(select 1 from ap_class_vouchers where signup_id = si.signup_id)
		  |and si.order_id = ?
		  |
		  |order by 7,2
		  |""".stripMargin

	override val params: List[String] = List(
		orderId.toString,
		orderId.toString,
		orderId.toString,
		orderId.toString,
		orderId.toString,
		orderId.toString,
		orderId.toString,
		orderId.toString,
		orderId.toString,
		orderId.toString
	)
}

case class FullCartItemResult(
	itemNameHTML: String,
	itemId: Double,
	itemType: String,
	nameFirst: Option[String],
	nameLast: Option[String],
	price: Double,
	displayOrder: Double,
	orderId: Int,
	fundId: Option[Int],
	inMemoryOf: Option[String]
)

object FullCartItemResult {
	implicit val format = Json.format[FullCartItemResult]
}
