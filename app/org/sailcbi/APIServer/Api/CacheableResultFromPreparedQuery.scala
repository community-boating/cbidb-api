package org.sailcbi.APIServer.Api

import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.PreparedQueries.HardcodedQueryForSelectCastableToJSObject
import org.sailcbi.APIServer.Services.Authentication.NonMemberUserType
import org.sailcbi.APIServer.Services.Exception.UnauthorizedAccessException
import org.sailcbi.APIServer.Services.{CacheBroker, PermissionsAuthority, PersistenceBroker}
import play.api.libs.json.{JsArray, JsObject}
import play.api.mvc.{Action, AnyContent, InjectedController}

import scala.concurrent.Future

trait CacheableResultFromPreparedQuery[T <: ParamsObject, U] extends CacheableResult[T, U] with InjectedController {
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

	protected def evaluate(ut: NonMemberUserType, params: T, pq: PQ)(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		try {
			PA.withRequestCache(ut, None, ParsedRequest(request), rc => {
				val cb: CacheBroker = rc.cb
				val pb = rc.pb
				getFuture(cb, pb, params, pq).map(s => {
					Ok(s).as("application/json")
				})
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
