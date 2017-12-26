package Services

import Storable._

abstract class PersistenceBroker private[Services] (rc: RequestCache) {
  // All public requests need to go through user type-based security
  final def getObjectById[T <: StorableClass](obj: StorableObject[T], id: Int): Option[T] = {
    if (entityVisible(obj)) getObjectByIdImplementation(obj, id)
    else None
  }
  final def getObjectsByIds[T <: StorableClass](obj: StorableObject[T], ids: List[Int], fetchSize: Int = 50): List[T] = {
    if (entityVisible(obj)) getObjectsByIdsImplementation(obj, ids)
    else Nil
  }
  final def getObjectsByFilters[T <: StorableClass](obj: StorableObject[T], filters: List[Filter], fetchSize: Int = 50): List[T] = {
    if (entityVisible(obj)) getObjectsByFiltersImplementation(obj, filters)
    else Nil
  }
  final def getAllObjectsOfClass[T <: StorableClass](obj: StorableObject[T]): List[T] = {
    if (entityVisible(obj)) getAllObjectsOfClassImplementation(obj)
    else Nil
  }
  final def commitObjectToDatabase(i: StorableClass): Unit = {
    if (entityVisible(i.getCompanion)) commitObjectToDatabaseImplementation(i)
    else throw new Exception("commitObjectToDatabase request denied due to entity security")
  }

  // Implementations of PersistenceBroker should implement these.  Assume user type security has already been passed if you're calling these
  protected def getObjectByIdImplementation[T <: StorableClass](obj: StorableObject[T], id: Int): Option[T]
  protected def getObjectsByIdsImplementation[T <: StorableClass](obj: StorableObject[T], ids: List[Int], fetchSize: Int = 50): List[T]
  protected def getObjectsByFiltersImplementation[T <: StorableClass](obj: StorableObject[T], filters: List[Filter], fetchSize: Int = 50): List[T]
  protected def getAllObjectsOfClassImplementation[T <: StorableClass](obj: StorableObject[T]): List[T]
  protected def commitObjectToDatabaseImplementation(i: StorableClass): Unit

  // TODO: implement some IDs
  private def entityVisible[T <: StorableClass](obj: StorableObject[T]): Boolean = obj.getVisiblity(rc.authenticatedUserType) match {
    case VISIBLE_ALWAYS => true
    case VISIBLE_NEVER | VISIBLE_SOME_IDS(_) => false
  }
}
