package IO.PreparedQueries.Apex

import java.sql.ResultSet

import IO.PreparedQueries.PreparedQueryForSelect
import Services.Authentication.ApexUserType

class GetCartDetailsForOrderId(orderId: Int) extends PreparedQueryForSelect[GetCartDetailsForOrderIdResult](Set(ApexUserType)) {
  val getQuery: String =
    s"""
      |select order_id, round(sum(price)*100)
      |from full_cart
      |where order_id = $orderId
      |group by order_id
      |
    """.stripMargin

  override def mapResultSetRowToCaseObject(rs: ResultSet): GetCartDetailsForOrderIdResult = GetCartDetailsForOrderIdResult(
    rs.getInt(1),
    rs.getInt(2)
  )
}

case class GetCartDetailsForOrderIdResult (
  orderId: Int,
  priceInCents: Int
)