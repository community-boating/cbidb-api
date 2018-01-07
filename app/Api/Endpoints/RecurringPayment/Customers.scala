package Api.Endpoints.RecurringPayment

import javax.inject.Inject

import CbiUtil.ValidateRequest
import Services.{CacheBroker, PermissionsAuthority, PersistenceBroker, RequestCache}
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.{ExecutionContext, Future}

class Customers @Inject() (ws: WSClient) (implicit exec: ExecutionContext) extends Controller {
  // Get all customers
  def get(): Action[AnyContent] = Action.async { request =>
    val rc: RequestCache = PermissionsAuthority.getRequestCache(request)
    Chargify.Request.getCustomers(rc, ws).map(s => {
      Ok(s).as("application/json")
    })
  }

  def post(): Action[AnyContent] = Action.async { request =>
    Customers.createCustomerValidator(request) match {
      case None => Future{ Ok("bad req") }
      case Some(params) => {
        val rc: RequestCache = PermissionsAuthority.getRequestCache(request)
        Chargify.Request.createCustomer(rc, ws).map(s => {
          Ok(s).as("application/json")
        })
      }
    }
  }
}

object Customers {
  val createCustomerValidator: ValidateRequest.RequestToParamsFunction = ValidateRequest.post(Set(("personId", true)))
}