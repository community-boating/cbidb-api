package Api.Endpoints.ReportingAPI

import javax.inject.Inject

import Reporting.ReportableStorableClass
import Services.CacheBroker
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.ExecutionContext
import scala.reflect.runtime._
import scala.reflect.runtime.{universe => ru}

class GetFiltersForStorable @Inject()(cb: CacheBroker, ws: WSClient)(implicit exec: ExecutionContext) extends Controller {
  private lazy val universeMirror = ru.runtimeMirror(getClass.getClassLoader)

  def get(storableName: String): Action[AnyContent] = Action {
    println("looking for " + storableName)
    val pkgRegexp = "package (.*)".r
    val classRegexp = "class (.*)".r
    try {
      val c = Class.forName("Entities." + storableName).asSubclass(classOf[ReportableStorableClass])
      val i = c.getDeclaredConstructor().newInstance()
      val subClasses = currentMirror.staticClass(i.ReportingFilterSubclass.getName).knownDirectSubclasses.map(sc => {
        val pkg: String = i.ReportingFilterSubclass.getPackage.toString match {
          case pkgRegexp(v) => v
          case _ => throw new Exception("couldn't get package")
        }
        val classSymbol: String = sc.toString match {
          case classRegexp(v) => v
          case _ => throw new Exception("couldn't get class symbol")
        }
        Class.forName(pkg + "." + classSymbol)
      })
      
      Ok("dfghdfg")
    } catch {
      case e: Throwable => {
        println(e)
        Ok("Nope not a thing")
      }
    }
  }
}
