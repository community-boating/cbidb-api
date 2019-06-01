package Reporting.ReportingFilters

import Services.PersistenceBroker
import Storable.StorableClass

abstract class ReportingFilterFactory[T <: StorableClass] {
	type ArgDefinition = (ReportingFilterArgType, String) // type and default value
	val displayName: String

	def getFilter(pb: PersistenceBroker, args: String): ReportingFilter[T]

	val argDefinitions: List[ArgDefinition]
}

object ReportingFilterFactory {


	class BadReportingFilterFactoryArgumentException(
															private val message: String = "",
															private val cause: Throwable = None.orNull
													) extends Exception(message, cause)

}

