package org.sailcbi.APIServer.Api.Endpoints.Stripe

import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

// Called by apex to get details so apex can save to prod db itself.  Legacy
class GetTokenDetails @Inject()(ws: WSClient)(implicit val exec: ExecutionContext) extends InjectedController {
	def get(token: String)(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { req => {
		Future(Gone)
		}
	}
}
