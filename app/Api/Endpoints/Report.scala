package Api.Endpoints

import javax.inject.Inject

import CbiUtil.Profiler
import Reporting.ReportingFilters.ApClassInstance.{ApClassInstanceFilter, ApClassInstanceFilterType, ApClassInstanceFilterYear}
import Services.{CacheBroker, PersistenceBroker}
import play.api.inject.ApplicationLifecycle
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.ExecutionContext

class Report @Inject() (lifecycle: ApplicationLifecycle, cb: CacheBroker, pb: PersistenceBroker)(implicit exec: ExecutionContext) extends Controller {
  def get(): Action[AnyContent] = Action {
    val profiler = new Profiler
    val thisYear: ApClassInstanceFilter = new ApClassInstanceFilterYear(pb, 2017)
    profiler.lap("did this year")
    val jibClasses: ApClassInstanceFilter = new ApClassInstanceFilterType(pb, 7)
    profiler.lap("did jib 1")
    val jib2Classes: ApClassInstanceFilter = new ApClassInstanceFilterType(pb, 8)
    profiler.lap("did jib 2")

    println("# instances this year: " + thisYear.primaryKeyValues.size)
    println("# jib classes: " + jibClasses.primaryKeyValues.size)
    println("# jib 2 classes: " + jib2Classes.primaryKeyValues.size)
    println("# jib this year: " + thisYear.and(jibClasses).primaryKeyValues.size)
    println("# jib 2 this year: " + thisYear.and(jib2Classes).primaryKeyValues.size)
    println(" any jib this year: " + thisYear.and(jibClasses.or(jib2Classes)).primaryKeyValues.size)

    profiler.lap("done")

    Ok("hi")
  }
}
