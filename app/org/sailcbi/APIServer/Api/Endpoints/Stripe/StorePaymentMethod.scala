package org.sailcbi.APIServer.Api.Endpoints.Stripe

import com.coleji.neptune.API
import com.coleji.neptune.API.{ResultError, ValidationOk}
import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import com.coleji.neptune.Util.{CriticalError, NetSuccess, ValidationError}
import org.sailcbi.APIServer.Entities.JsFacades.Stripe.StripeError
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.UserTypes.{LockedRequestCacheWithSquareController, MemberRequestCache, ProtoPersonRequestCache}
import play.api.libs.json.{JsBoolean, JsObject, JsValue, Json}
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent, InjectedController, Result}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class StorePaymentMethod @Inject()(implicit exec: ExecutionContext, ws: WSClient) extends InjectedController {
	def postAP()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { req =>
		Future(Gone)
	}
	def postJP(juniorId: Option[Int])(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { req =>
		Future(Gone)
	}

	def postAutoDonate()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { req =>
		Future(Gone)
	}

	def postDonate()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { req =>
		Future(Gone)
	}

	private def post(rc: LockedRequestCacheWithSquareController, paymentMethodId: String, retryLatePayments: Boolean, adultPersonId: Int, juniorId: Option[Int])(implicit PA: PermissionsAuthority): Future[Result] =  {
		Future(Gone)
	}

}
