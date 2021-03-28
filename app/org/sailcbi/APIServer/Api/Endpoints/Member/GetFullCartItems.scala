package org.sailcbi.APIServer.Api.Endpoints.Member

import com.coleji.framework.Core.{CacheBroker, ParsedRequest, PermissionsAuthority}
import org.sailcbi.APIServer.Entities.MagicIds.ORDER_NUMBER_APP_ALIAS
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.IO.PreparedQueries.Member.FullCart
import org.sailcbi.APIServer.UserTypes.ProtoPersonRequestCache
import play.api.libs.json.{JsArray, Json}
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetFullCartItems @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def get(program: String)(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCacheMember(parsedRequest, rc => {
			val cb: CacheBroker = rc.cb
			val personId = rc.getAuthedPersonId()
			val orderId = PortalLogic.getOrderId(rc, personId, program)

			val fullCartItemsQuery = new FullCart(orderId)

			val resultObj = rc.executePreparedQueryForSelect(fullCartItemsQuery)
			Future(Ok(Json.toJson(resultObj)))
		})
	}

	def getDonate()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)

		if (parsedRequest.cookies.get(ProtoPersonRequestCache.COOKIE_NAME).isDefined) {
			PA.withRequestCache(ProtoPersonRequestCache)(None, parsedRequest, rc => {
				val cb: CacheBroker = rc.cb
				rc.getAuthedPersonId() match {
					case None => Future(Ok(JsArray()))
					case Some(personId: Int) => {
						val orderId = PortalLogic.getOrderId(rc, personId, ORDER_NUMBER_APP_ALIAS.DONATE)

						val fullCartItemsQuery = new FullCart(orderId)

						val resultObj = rc.executePreparedQueryForSelect(fullCartItemsQuery)
						Future(Ok(Json.toJson(resultObj)))
					}
				}
			})
		} else {
			Future(Ok(JsArray()))
		}
	}
}
