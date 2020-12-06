package org.sailcbi.APIServer.Api.Endpoints

import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class Ping @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def get(): Action[AnyContent] = Action { _ => {
		Ok("pong")
	}}
}
