package org.sailcbi.APIServer.Api.Endpoints.Stripe

import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import com.coleji.neptune.Util.{CriticalError, Succeeded}
import org.sailcbi.APIServer.UserTypes.ApexRequestCache
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SyncLocalDBWithStripe @Inject()(ws: WSClient)(implicit exec: ExecutionContext) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { req => {
		Future(Gone)
		}
	}
}