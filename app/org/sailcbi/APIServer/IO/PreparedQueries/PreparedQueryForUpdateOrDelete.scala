package org.sailcbi.APIServer.IO.PreparedQueries

import org.sailcbi.APIServer.Services.Authentication.UserTypeObject

abstract class PreparedQueryForUpdateOrDelete(
	override val allowedUserTypes: Set[UserTypeObject[_]],
	override val useTempSchema: Boolean = false
) extends HardcodedQueryForUpdateOrDelete(allowedUserTypes, useTempSchema) {
	val params: List[String] = List.empty
	val preparedParams: List[PreparedValue] = List.empty

	def getParams: List[PreparedValue] = {
		if (params.nonEmpty && preparedParams.isEmpty) {
			// legacy mode, use the old string params
			params.map(PreparedString)
		} else {
			preparedParams
		}
	}
}
