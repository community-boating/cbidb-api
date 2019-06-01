package Api

import CbiUtil.ParsedRequest
import IO.PreparedQueries.HardcodedQueryForSelectCastableToJSObject
import Services.Authentication.UserType
import Services.PermissionsAuthority.UnauthorizedAccessException
import Services.{CacheBroker, PersistenceBroker}
import play.api.libs.json.{JsArray, JsObject}
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.Future

trait CacheableResultFromPreparedQuery[T <: ParamsObject, U] extends CacheableResult[T, U] with AuthenticatedRequest {
	type PQ = HardcodedQueryForSelectCastableToJSObject[U]

	protected def getFuture(cb: CacheBroker, pb: PersistenceBroker, params: T, pq: PQ): Future[String] = {
		val calculateValue: (() => Future[JsObject]) = () => Future {
			val queryResults = pb.executePreparedQueryForSelect(pq)

			JsObject(Map(
				"rows" -> JsArray(queryResults.map(r => pq.mapCaseObjectToJsArray(r))),
				"metaData" -> pq.getColumnsNamesAsJSObject
			))
		}
		getFuture(cb, pb, params, calculateValue)
	}

	protected def evaluate(ut: UserType, params: T, pq: PQ): Action[AnyContent] = Action.async { request =>
		try {
			val rc = getRC(ut, ParsedRequest(request))
			val cb: CacheBroker = rc.cb
			val pb = rc.pb
			getFuture(cb, pb, params, pq).map(s => {
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
