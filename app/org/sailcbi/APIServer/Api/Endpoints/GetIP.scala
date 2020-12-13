package org.sailcbi.APIServer.Api.Endpoints

import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class GetIP @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def get(): Action[AnyContent] = Action { req => {
		try {
			Ok(req.headers("X-Forwarded-For"))
		} catch {
			case _: Throwable => Ok("Error.")
		}
	}
	}
}
