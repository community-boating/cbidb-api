package org.sailcbi.APIServer.Api.Endpoints.Symon

import org.sailcbi.APIServer.CbiUtil._
import org.sailcbi.APIServer.IO.PreparedQueries.Symon.StoreSymonRun
import org.sailcbi.APIServer.Services.Authentication.SymonUserType
import org.sailcbi.APIServer.Services.PermissionsAuthority
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent, InjectedController, Result}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PostSymonRun @Inject()(ws: WSClient)(implicit val exec: ExecutionContext) extends InjectedController {
	def post(): Action[AnyContent] = Action.async { r => doPost(ParsedRequest(r)) }

	def doPost(req: ParsedRequest)(implicit PA: PermissionsAuthority): Future[Result] = {
		val logger = PA.logger

		PA.withRequestCache(SymonUserType)(None, req, rc => {
			val pb = rc.pb

			if (
				req.postParams("symon-program") == "can-email" &&
						req.postParams("symon-status") != "1"
			) {
				logger.error("Symon client " + req.postParams("symon-host") + " reported failure to send emails")
				Future {
					Ok("alarm raised.")
				}
			} else {
				pb.executePreparedQueryForInsert(new StoreSymonRun(
					req.postParams("symon-host"),
					req.postParams("symon-program"),
					req.postParams("symon-argString"),
					req.postParams("symon-status").toInt,
					req.postParams("symon-mac"),
					req.postParams.get("symon-version")
				))

				Future {
					Ok("inserted.")
				}
			}
		})
	}
}