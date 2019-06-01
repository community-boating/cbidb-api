package Api.Endpoints.Symon

import Api.AuthenticatedRequest
import CbiUtil._
import IO.PreparedQueries.Symon.StoreSymonRun
import Services.Authentication.SymonUserType
import Services.PermissionsAuthority
import javax.inject.Inject
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent, Result}

import scala.concurrent.{ExecutionContext, Future}

class PostSymonRun @Inject()(ws: WSClient)(implicit exec: ExecutionContext) extends AuthenticatedRequest {
	def post(): Action[AnyContent] = Action.async { r => doPost(ParsedRequest(r)) }

	def doPost(req: ParsedRequest): Future[Result] = {
		val logger = PermissionsAuthority.logger

		try {
			val rc = getRC(SymonUserType, req)
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
		} catch {
			case _: Throwable => Future {
				Ok("Unable to log symon run")
			}
		}

	}
}