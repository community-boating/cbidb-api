package Api.Endpoints.RecurringPayment

import javax.inject.Inject

import CbiUtil.ValidateRequest
import Entities.EntityDefinitions.Person
import Services.{PermissionsAuthority, RequestCache}
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.{ExecutionContext, Future}

class PaymentProfiles @Inject() (ws: WSClient) (implicit exec: ExecutionContext) extends Controller {
  // Get all payment profiles
  def get(): Action[AnyContent] = Action.async { request =>
    val rc: RequestCache = PermissionsAuthority.getRequestCache(request)
    Chargify.Request.getPaymentProfiles(rc, ws).map(s => {
      Ok(s).as("application/json")
    })
  }

  def post(): Action[AnyContent] = Action.async { request =>
    Customers.createCustomerValidator(request) match {
      case None => Future{ Ok("bad req") }
      case Some(params) => {
        val rc: RequestCache = PermissionsAuthority.getRequestCache(request)
        val pb = rc.pb
        val p = pb.getObjectById(Person, params("personId").toInt).get
        Chargify.Request.createCustomer(p, rc, ws).map(s => {
          Ok(s.toString).as("application/json")
        })
      }
    }
  }
}