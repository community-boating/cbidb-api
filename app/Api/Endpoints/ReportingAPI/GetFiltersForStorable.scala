package Api.Endpoints.ReportingAPI

import javax.inject.Inject

import Services.CacheBroker
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.ExecutionContext
import scala.reflect.runtime.{universe => ru}

class GetFiltersForStorable @Inject()(cb: CacheBroker, ws: WSClient)(implicit exec: ExecutionContext) extends Controller {
  private lazy val universeMirror = ru.runtimeMirror(getClass.getClassLoader)

  def get(storableName: String): Action[AnyContent] = Action {
    Ok("dfg")
  }
}
