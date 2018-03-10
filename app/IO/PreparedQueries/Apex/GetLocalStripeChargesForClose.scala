package IO.PreparedQueries.Apex

import java.sql.ResultSet

import IO.PreparedQueries.PreparedQuery
import Services.Authentication.{ApexUserType, UserType}
import Stripe.JsFacades.{Charge, ChargeMetadata}

class GetLocalStripeChargesForClose(closeId: Int) extends PreparedQuery[Charge]{
  override val allowedUserTypes: Set[UserType] = Set(ApexUserType)
  val getQuery: String =
    s"""
       |select CHARGE_ID, AMOUNT, CLOSE_ID, orderId, token, CREATED_EPOCH, PAID, status
       |from STRIPE_CHARGES
       |where CLOSE_ID = $closeId
       |order by created_datetime
       |
    """.stripMargin

  override def mapResultSetRowToCaseObject(rs: ResultSet): Charge = {
    val cmd = ChargeMetadata(
      closeId = Some(rs.getInt(3).toString),
      orderId = Some(rs.getInt(4).toString),
      token = Some(rs.getString(5)),
      cbiInstance = None
    )

    Charge(
      id = rs.getString(1),
      amount = rs.getInt(2),
      metadata = cmd,
      created = rs.getInt(6),
      paid = rs.getString(7) == "Y",
      status = rs.getString(8)
    )
  }
}
