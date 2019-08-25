package org.sailcbi.APIServer.Api

import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.Services.Authentication.NonMemberUserType
import org.sailcbi.APIServer.Services.PermissionsAuthority.UnauthorizedAccessException
import org.sailcbi.APIServer.Services.{CacheBroker, PersistenceBroker}
import play.api.libs.json.{JsObject, JsValue}
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.Future

trait CacheableResultFromRemoteRequest[T <: ParamsObject, U] extends CacheableResult[T, U] with AuthenticatedRequest {
	private def getFuture(cb: CacheBroker, pb: PersistenceBroker, params: T, ws: WSClient, url: String): Future[String] = {
		val calculateValue: (() => Future[JsObject]) = () => {
			val request: WSRequest = ws.url(url)
			println("*** Making remote web request!")
			val futureResponse: Future[WSResponse] = request.get()
			val jsonFuture: Future[JsObject] = futureResponse.map(_.json match {
				case json: JsObject => json
				case v: JsValue => new JsObject(Map("data" -> v))
				case _ => throw new Exception("Received unparsable json response")
			})
			jsonFuture
		}
		getFuture(cb, pb, params, calculateValue)
	}

	protected def evaluate(ut: NonMemberUserType, params: T, ws: WSClient, url: String): Action[AnyContent] = Action.async { request =>
		try {
			val rc = getRC(ut, ParsedRequest(request))
			val cb: CacheBroker = rc.cb
			val pb = rc.pb
			getFuture(cb, pb, params, ws, url).map(s => {
				Ok(s).as("application/json")
			})
		} catch {
			case _: UnauthorizedAccessException => Future {
				Ok("Access Denied")
			}
			case _: Throwable => Future {
				Ok("Internal Error")
			}
		}
	}
}