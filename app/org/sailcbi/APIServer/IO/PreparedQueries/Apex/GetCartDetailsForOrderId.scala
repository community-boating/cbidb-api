package org.sailcbi.APIServer.IO.PreparedQueries.Apex

import java.sql.ResultSet

import org.sailcbi.APIServer.IO.PreparedQueries.HardcodedQueryForSelect
import org.sailcbi.APIServer.Services.Authentication.ApexUserType
import org.sailcbi.APIServer.Services.ResultSetWrapper

class GetCartDetailsForOrderId(orderId: Int) extends HardcodedQueryForSelect[GetCartDetailsForOrderIdResult](Set(ApexUserType)) {
	val getQuery: String =
		s"""
		   |select order_id, round(sum(price)*100)
		   |from full_cart
		   |where order_id = $orderId
		   |group by order_id
		   |
    """.stripMargin

	override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): GetCartDetailsForOrderIdResult = GetCartDetailsForOrderIdResult(
		rs.getInt(1),
		rs.getInt(2)
	)
}

case class GetCartDetailsForOrderIdResult(
	 orderId: Int,
	 priceInCents: Int
)