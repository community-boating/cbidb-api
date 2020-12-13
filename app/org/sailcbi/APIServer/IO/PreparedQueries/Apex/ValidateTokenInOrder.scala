package org.sailcbi.APIServer.IO.PreparedQueries.Apex

import org.sailcbi.APIServer.IO.PreparedQueries.PreparedQueryForSelect
import org.sailcbi.APIServer.Services.Authentication.ApexUserType
import org.sailcbi.APIServer.Services.ResultSetWrapper

import java.time.LocalDateTime

class ValidateTokenInOrder(orderId: Int, token: String) extends PreparedQueryForSelect[ValidateTokenInOrderResult](Set(ApexUserType)) {
	val getQuery: String =
		s"""
		   |select order_id, token, created_datetime
		   |from stripe_tokens
		   |where order_id = ?
		   |and token = ?
		   |order by created_datetime
		   |
    """.stripMargin

	override val params = List(orderId.toString, token)

	override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): ValidateTokenInOrderResult = ValidateTokenInOrderResult(
		rs.getInt(1),
		rs.getString(2),
		rs.getLocalDateTime(3)
	)
}

case class ValidateTokenInOrderResult(
	orderId: Int,
	token: String,
	created: LocalDateTime
)