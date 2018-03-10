package Api.Endpoints

import javax.inject.Inject

import Services.PermissionsAuthority
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.ExecutionContext

class Ping @Inject()(implicit exec: ExecutionContext) extends Controller {
  def get(): Action[AnyContent] = Action {_ => {
    val pb1 = PermissionsAuthority.getRootPB.get
    println("$$$$$$" +pb1.toString)
    Ok("pong")
  }}
}
