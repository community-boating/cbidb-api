package Api.Endpoints.Stripe

import javax.inject.Inject

import play.api.libs.ws.WSClient

import scala.concurrent.ExecutionContext

class CreateChargeFromToken @Inject() (ws: WSClient) (implicit exec: ExecutionContext) /*extends AuthenticatedRequest(ApexUserType)*/ {
 /* def post(): Action[AnyContent] = Action.async {r => doPost(ParsedRequest(r))}

  def doPost(req: ParsedRequest): Future[Result] = {
    // TODO: wrap in try, e.g. what if orderID doesnt exist
    val rc = getRC(req.headers, req.cookies)
    val pb = rc.pb
    val params = req.postParams
    val token: String = params("token")
    val orderId: Int = params("orderId").toInt

    // TODO: price isnt coming out correctly
    // TODO: also validate that token is associated with requested orderid
    val orderDetails: GetCartDetailsForOrderIdResult = pb.executePreparedQuery(new GetCartDetailsForOrderId(orderId)).head

    val url = "https://api.stripe.com/v1/charges"

    val stripeRequest: WSRequest = ws.url(url).withAuth(PermissionsAuthority.secrets.stripeAPIKey.get(rc), "", WSAuthScheme.BASIC)
    val futureResponse: Future[WSResponse] = stripeRequest.post(Map(
      "amount" -> orderDetails.priceInCents.toString,
      "currency" -> "usd",
      "source" -> token,
      "description" -> ("Charge for orderId " + orderId + " time " + ServerStateContainer.get.nowDateTImeString)
    ))
    futureResponse.map(r => {
      println(r.json.toString())
      def parseAsSuccess: Try[String] = Try {
        val chargeObject = Stripe.JsFacades.Charge(r.json)
        List("success", chargeObject.id, chargeObject.amount).mkString("$$")
      }
      def parseAsFailure: Try[String] = Try {
        val errorObject = Stripe.JsFacades.Error(r.json)
        List("failure", errorObject.`type`, errorObject.message).mkString("$$")
      }

      def msgTry: Try[String] = parseAsSuccess orElse parseAsFailure

      val msg: String = msgTry.getOrElse(List("failure", "Unknown error").mkString("$$"))

      Ok(msg)
    })
  }*/
}

