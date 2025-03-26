package org.sailcbi.APIServer.Api.Endpoints.Stripe

import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import org.sailcbi.APIServer.UserTypes.PublicRequestCache
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

// Called by react to get details and save to prod DB in one swoop
class SaveTokenDetails @Inject()(ws: WSClient)(implicit val exec: ExecutionContext) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { req =>
		val pr = ParsedRequest(req)
		PA.withRequestCache(PublicRequestCache)(None, pr, rc => {
			Future(Gone)
		})
	}

	case class SaveTokenDetailsRequestShape(token: String, orderId: Int)

	object SaveTokenDetailsRequestShape {
		implicit val format = Json.format[SaveTokenDetailsRequestShape]

		def apply(v: JsValue): SaveTokenDetailsRequestShape = v.as[SaveTokenDetailsRequestShape]
	}
}
