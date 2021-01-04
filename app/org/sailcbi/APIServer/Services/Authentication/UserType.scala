package org.sailcbi.APIServer.Services.Authentication

import org.sailcbi.APIServer.Services.PersistenceBroker
import org.sailcbi.APIServer.Storable.Fields.DatabaseField
import org.sailcbi.APIServer.Storable.{Filter, StorableClass, StorableObject}

abstract class UserType(val userName: String) {
	// Given a username (and an unrestricted PersistenceBroker), get the (hashingGeneration, psHash) that is active for the user
	def getPwHashForUser(rootPB: PersistenceBroker[RootUserType]): Option[(Int, String)] = None

	def name: String = this.getClass.getName

	def companion: UserTypeObject[_]

	def getObjectById[T <: StorableClass, U <: UserType](userName: String, pb: PersistenceBroker[U])(obj: StorableObject[T], id: Int): Option[T] =
		throw new Exception("getObjectById not overridden by " + this.getClass.getName)

	def getObjectsByIds[T <: StorableClass, U <: UserType](userName: String, pb: PersistenceBroker[U])(obj: StorableObject[T], ids: List[Int], fetchSize: Int = 50): List[T] =
		throw new Exception("getObjectsByIds not overridden by " + this.getClass.getName)

	def getObjectsByFilters[T <: StorableClass, U <: UserType](userName: String, pb: PersistenceBroker[U])(obj: StorableObject[T], filters: List[String => Filter], fetchSize: Int = 50): List[T] =
		throw new Exception("getObjectsByFilters not overridden by " + this.getClass.getName)

	def getAllObjectsOfClass[T <: StorableClass, U <: UserType](userName: String, pb: PersistenceBroker[U])(obj: StorableObject[T], fields: Option[List[DatabaseField[_]]] = None): List[T] =
		throw new Exception("getAllObjectsOfClass not overridden by " + this.getClass.getName)

	def commitObjectToDatabase[U <: UserType](userName: String, pb: PersistenceBroker[U])(i: StorableClass): Unit =
		throw new Exception("commitObjectToDatabase not overridden by " + this.getClass.getName)
}
