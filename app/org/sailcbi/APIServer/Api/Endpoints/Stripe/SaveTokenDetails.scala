package org.sailcbi.APIServer.Api.Endpoints.Stripe

import com.coleji.neptune.Core.PermissionsAuthority

import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

// Called by react to get details and save to prod DB in one swoop
class SaveTokenDetails @Inject()(ws: WSClient)(implicit val exec: ExecutionContext) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { req =>
		Future(Gone)
	}

}
