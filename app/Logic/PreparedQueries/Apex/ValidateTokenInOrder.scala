package Logic.PreparedQueries.Apex

import java.sql.ResultSet
import java.time.LocalDateTime

import Api.ApiDataObject
import Logic.PreparedQueries.PreparedQuery
import Services.Authentication.{ApexUserType, UserType}

class ValidateTokenInOrder(orderId: Int, token: String) extends PreparedQuery[ValidateTokenInOrderResult]{
  override val allowedUserTypes: Set[UserType] = Set(ApexUserType)
  val getQuery: String =
    s"""
       |select order_id, token, created_datetime
       |from stripe_tokens
       |where order_id = $orderId
       |and token = '$token'
       |order by created_datetime
       |
    """.stripMargin

  override def mapResultSetRowToCaseObject(rs: ResultSet): ValidateTokenInOrderResult = ValidateTokenInOrderResult(
    rs.getInt(1),
    rs.getString(2),
    rs.getTimestamp(3).toLocalDateTime
  )
}

case class ValidateTokenInOrderResult (
  orderId: Int,
  token: String,
  created: LocalDateTime
) extends ApiDataObject