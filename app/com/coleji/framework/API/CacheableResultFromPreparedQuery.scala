package com.coleji.framework.API

import com.coleji.framework.Core.{CacheBroker, PermissionsAuthority, RequestCache, RequestCacheObject}
import com.coleji.framework.Exception.UnauthorizedAccessException
import com.coleji.framework.IO.PreparedQueries.HardcodedQueryForSelectCastableToJSObject
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import play.api.libs.json.{JsArray, JsObject}
import play.api.mvc.{Action, AnyContent, InjectedController}

import scala.concurrent.Future

trait CacheableResultFromPreparedQuery[T <: ParamsObject, U] extends CacheableResult[T, U] with InjectedController {
	type PQ = HardcodedQueryForSelectCastableToJSObject[U]

	protected def getFuture(cb: CacheBroker, rc: RequestCache, params: T, pq: PQ): Future[String] = {
		val calculateValue: (() => Future[JsObject]) = () => Future {
			val queryResults = rc.executePreparedQueryForSelect(pq)

			JsObject(Map(
				"rows" -> JsArray(queryResults.map(r => pq.mapCaseObjectToJsArray(r))),
				"metaData" -> pq.getColumnsNamesAsJSObject
			))
		}
		getFuture(cb, rc, params, calculateValue)
	}

	protected def evaluate[T_User <: RequestCache](ut: RequestCacheObject[T_User], params: T, pq: PQ)(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		try {
			PA.withRequestCache[T_User](ut)(None, ParsedRequest(request), rc => {
				val cb: CacheBroker = rc.cb
				getFuture(cb, rc, params, pq).map(s => {
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
