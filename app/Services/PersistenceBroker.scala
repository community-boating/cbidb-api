package Services

import CbiUtil.TestUserType
import IO.PreparedQueries.{HardcodedQueryForInsert, HardcodedQueryForSelect, HardcodedQueryForUpdateOrDelete}
import Services.PermissionsAuthority.UnauthorizedAccessException
import Storable._

// TODO: decide on one place for all the fetchSize defaults and delete the rest
abstract class PersistenceBroker private[Services](rc: RequestCache, preparedQueriesOnly: Boolean) {
	// All public requests need to go through user type-based security
	final def getObjectById[T <: StorableClass](obj: StorableObject[T], id: Int): Option[T] = {
		if (preparedQueriesOnly) throw new UnauthorizedAccessException("Server is in Prepared Queries Only mode.")
		else if (entityVisible(obj)) getObjectByIdImplementation(obj, id)
		else throw new UnauthorizedAccessException("Access to entity " + obj.entityName + " blocked for userType " + rc.auth.userType)
	}

	final def getObjectsByIds[T <: StorableClass](obj: StorableObject[T], ids: List[Int], fetchSize: Int = 50): List[T] = {
		if (preparedQueriesOnly) throw new UnauthorizedAccessException("Server is in Prepared Queries Only mode.")
		else if (entityVisible(obj)) getObjectsByIdsImplementation(obj, ids)
		else throw new UnauthorizedAccessException("Access to entity " + obj.entityName + " blocked for userType " + rc.auth.userType)
	}

	final def getObjectsByFilters[T <: StorableClass](obj: StorableObject[T], filters: List[Filter], fetchSize: Int = 50): List[T] = {
		if (preparedQueriesOnly) throw new UnauthorizedAccessException("Server is in Prepared Queries Only mode.")
		else if (entityVisible(obj)) getObjectsByFiltersImplementation(obj, filters)
		else throw new UnauthorizedAccessException("Access to entity " + obj.entityName + " blocked for userType " + rc.auth.userType)
	}

	final def getAllObjectsOfClass[T <: StorableClass](obj: StorableObject[T]): List[T] = {
		if (preparedQueriesOnly) throw new UnauthorizedAccessException("Server is in Prepared Queries Only mode.")
		else if (entityVisible(obj)) getAllObjectsOfClassImplementation(obj)
		else throw new UnauthorizedAccessException("Access to entity " + obj.entityName + " blocked for userType " + rc.auth.userType)
	}

	final def commitObjectToDatabase(i: StorableClass): Unit = {
		if (preparedQueriesOnly) throw new UnauthorizedAccessException("Server is in Prepared Queries Only mode.")
		else if (entityVisible(i.getCompanion)) commitObjectToDatabaseImplementation(i)
		else throw new UnauthorizedAccessException("commitObjectToDatabase request denied due to entity security")
	}

	final def executePreparedQueryForSelect[T](pq: HardcodedQueryForSelect[T], fetchSize: Int = 50): List[T] = {
		if (TestUserType(pq.allowedUserTypes, rc.auth.userType)) executePreparedQueryForSelectImplementation(pq, fetchSize)
		else throw new UnauthorizedAccessException("executePreparedQueryforSelect denied to userType " + rc.auth.userType)
	}

	final def executePreparedQueryForInsert(pq: HardcodedQueryForInsert): Option[String] = {
		if (TestUserType(pq.allowedUserTypes, rc.auth.userType)) executePreparedQueryForInsertImplementation(pq)
		else throw new UnauthorizedAccessException("executePreparedQueryForInsert denied to userType " + rc.auth.userType)
	}

	final def executePreparedQueryForUpdateOrDelete(pq: HardcodedQueryForUpdateOrDelete): Int = {
		if (TestUserType(pq.allowedUserTypes, rc.auth.userType)) executePreparedQueryForUpdateOrDeleteImplementation(pq)
		else throw new UnauthorizedAccessException("executePreparedQueryForInsert denied to userType " + rc.auth.userType)
	}

	// Implementations of PersistenceBroker should implement these.  Assume user type security has already been passed if you're calling these
	protected def getObjectByIdImplementation[T <: StorableClass](obj: StorableObject[T], id: Int): Option[T]

	protected def getObjectsByIdsImplementation[T <: StorableClass](obj: StorableObject[T], ids: List[Int], fetchSize: Int = 50): List[T]

	protected def getObjectsByFiltersImplementation[T <: StorableClass](obj: StorableObject[T], filters: List[Filter], fetchSize: Int = 50): List[T]

	protected def getAllObjectsOfClassImplementation[T <: StorableClass](obj: StorableObject[T]): List[T]

	protected def commitObjectToDatabaseImplementation(i: StorableClass): Unit

	protected def executePreparedQueryForSelectImplementation[T](pq: HardcodedQueryForSelect[T], fetchSize: Int = 50): List[T]

	protected def executePreparedQueryForInsertImplementation(pq: HardcodedQueryForInsert): Option[String]

	protected def executePreparedQueryForUpdateOrDeleteImplementation(pq: HardcodedQueryForUpdateOrDelete): Int

	// TODO: implement some IDs
	private def entityVisible[T <: StorableClass](obj: StorableObject[T]): Boolean = obj.getVisiblity(rc.auth.userType).entityVisible
}
