package Services

import Storable._

abstract class PersistenceBroker {
  def getObjectById[T <: StorableClass](obj: StorableObject[T], id: Int) : Option[T]
  def getObjectsByIds[T <: StorableClass](obj: StorableObject[T], ids: List[Int], fetchSize: Int = 50): List[T]
  def getObjectsByFilters[T <: StorableClass](obj: StorableObject[T], filters: List[Filter], fetchSize: Int = 50): List[T]
}
