package com.coleji.framework.Export

import com.coleji.framework.Storable.StorableClass
import org.sailcbi.APIServer.Services.UnlockedRequestCache

abstract class ReportingFilterFactory[T <: StorableClass] {
	type ArgDefinition = (ReportingFilterArgType, String) // type and default value
	val displayName: String

	def getFilter(rc: UnlockedRequestCache, args: String): ReportingFilter[T]

	val argDefinitions: List[ArgDefinition]
}

object ReportingFilterFactory {


	class BadReportingFilterFactoryArgumentException(
		private val message: String = "",
		private val cause: Throwable = None.orNull
	) extends Exception(message, cause)

}

