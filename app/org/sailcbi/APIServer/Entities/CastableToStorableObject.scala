package org.sailcbi.APIServer.Entities

import org.sailcbi.APIServer.Services.Authentication.UserType

trait CastableToStorableObject[T <: CastableToStorableClass] {
	val apexTableName: String
	protected val persistenceFieldsMap: Map[String, T => String]
	val pkColumnName: String

	val allowedUserTypes: Set[UserType]

	lazy val persistenceFields: List[String] = persistenceFieldsMap.toList.map(t => t._1)

	def persistenceValues(t: T): Map[String, String] = persistenceFieldsMap.map(tup => (tup._1, tup._2(t)))

	val getId: T => String
}
