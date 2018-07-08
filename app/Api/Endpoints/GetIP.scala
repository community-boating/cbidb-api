package Api.Endpoints

import javax.inject.Inject
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.ExecutionContext

class GetIP @Inject()(implicit exec: ExecutionContext) extends Controller {
  def get(): Action[AnyContent] = Action {req => {
    try {
      Ok(req.headers("X-Forwarded-For"))
    } catch {
      case _: Throwable => Ok("Error.")
    }
  }}
}
