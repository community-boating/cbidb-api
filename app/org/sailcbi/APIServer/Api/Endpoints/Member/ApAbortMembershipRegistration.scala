package org.sailcbi.APIServer.Api.Endpoints.Member

import com.coleji.framework.API.{ValidationError, ValidationOk}
import com.coleji.framework.Core.PermissionsAuthority
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import play.api.libs.json.{JsBoolean, JsObject}
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ApAbortMembershipRegistration @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request => {
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCacheMember(parsedRequest, rc => {


			val memberId = rc.getAuthedPersonId()

			PortalLogic.apDeleteReservation(rc, memberId) match {
				case ValidationOk => Future(Ok(new JsObject(Map("success" -> JsBoolean(true)))))
				case e: ValidationError => Future(Ok(e.toResultError.asJsObject()))
			}
		})
	}}
}
