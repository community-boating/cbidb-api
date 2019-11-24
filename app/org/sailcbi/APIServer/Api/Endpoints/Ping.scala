package org.sailcbi.APIServer.Api.Endpoints

import javax.inject.Inject
import play.api.mvc.{Action, AnyContent, InjectedController}

import scala.concurrent.ExecutionContext

class Ping @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def get(): Action[AnyContent] = Action { _ => {
		Ok("pong")
	}}
}
