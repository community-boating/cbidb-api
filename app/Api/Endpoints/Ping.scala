package Api.Endpoints

import javax.inject.Inject
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.ExecutionContext

class Ping @Inject()(implicit exec: ExecutionContext) extends Controller {
  def get(): Action[AnyContent] = Action {_ => {
    Ok("pong")
  }}
}
