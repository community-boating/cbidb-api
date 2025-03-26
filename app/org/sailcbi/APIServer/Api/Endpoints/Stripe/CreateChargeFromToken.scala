package org.sailcbi.APIServer.Api.Endpoints.Stripe

import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent, InjectedController, Result}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}


class CreateChargeFromToken @Inject()(ws: WSClient)(implicit val exec: ExecutionContext) extends InjectedController {
	def post(): Action[AnyContent] = Action.async { r => doPost(ParsedRequest(r)) }

	def doPost(req: ParsedRequest)(implicit PA: PermissionsAuthority): Future[Result] = {
		Future(Gone)
	}
}

