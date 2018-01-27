package Services

import Logic.PreparedQueries.{PreparedQuery, PreparedQueryCaseResult}
import Services.PermissionsAuthority.UnauthorizedAccessException
import Storable._

// TODO: decide on one place for all the fetchSize defaults and delete the rest
abstract class PersistenceBroker private[Services] (rc: RequestCache) {
  // All public requests need to go through user type-based security
  final def getObjectById[T <: StorableClass](obj: StorableObject[T], id: Int): Option[T] = {
    if (entityVisible(obj)) getObjectByIdImplementation(obj, id)
    else throw new UnauthorizedAccessException("Access to entity " + obj.entityName + " blocked for userType " + rc.authenticatedUserType)
  }
  final def getObjectsByIds[T <: StorableClass](obj: StorableObject[T], ids: List[Int], fetchSize: Int = 50): List[T] = {
    if (entityVisible(obj)) getObjectsByIdsImplementation(obj, ids)
    else throw new UnauthorizedAccessException("Access to entity " + obj.entityName + " blocked for userType " + rc.authenticatedUserType)
  }
  final def getObjectsByFilters[T <: StorableClass](obj: StorableObject[T], filters: List[Filter], fetchSize: Int = 50): List[T] = {
    if (entityVisible(obj)) getObjectsByFiltersImplementation(obj, filters)
    else throw new UnauthorizedAccessException("Access to entity " + obj.entityName + " blocked for userType " + rc.authenticatedUserType)
  }
  final def getAllObjectsOfClass[T <: StorableClass](obj: StorableObject[T]): List[T] = {
    if (entityVisible(obj)) getAllObjectsOfClassImplementation(obj)
    else throw new UnauthorizedAccessException("Access to entity " + obj.entityName + " blocked for userType " + rc.authenticatedUserType)
  }
  final def commitObjectToDatabase(i: StorableClass): Unit = {
    if (entityVisible(i.getCompanion)) commitObjectToDatabaseImplementation(i)
    else throw new UnauthorizedAccessException("commitObjectToDatabase request denied due to entity security")
  }

  final def executePreparedQuery[T <: PreparedQueryCaseResult](pq: PreparedQuery[T], fetchSize: Int = 50): List[T] = {
    if (pq.allowedUserTypes.contains(rc.authenticatedUserType)) executePreparedQueryImplementation(pq, fetchSize)
    else throw new UnauthorizedAccessException("executePreparedQuery denied to userType " + rc.authenticatedUserType)
  }

  // Implementations of PersistenceBroker should implement these.  Assume user type security has already been passed if you're calling these
  protected def getObjectByIdImplementation[T <: StorableClass](obj: StorableObject[T], id: Int): Option[T]
  protected def getObjectsByIdsImplementation[T <: StorableClass](obj: StorableObject[T], ids: List[Int], fetchSize: Int = 50): List[T]
  protected def getObjectsByFiltersImplementation[T <: StorableClass](obj: StorableObject[T], filters: List[Filter], fetchSize: Int = 50): List[T]
  protected def getAllObjectsOfClassImplementation[T <: StorableClass](obj: StorableObject[T]): List[T]
  protected def commitObjectToDatabaseImplementation(i: StorableClass): Unit
  protected def executePreparedQueryImplementation[T <: PreparedQueryCaseResult](pq: PreparedQuery[T], fetchSize: Int = 50): List[T]


  // TODO: implement some IDs
  private def entityVisible[T <: StorableClass](obj: StorableObject[T]): Boolean = obj.getVisiblity(rc.authenticatedUserType).entityVisible
}
