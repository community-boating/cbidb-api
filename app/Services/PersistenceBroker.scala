package Services

import Storable._

abstract class PersistenceBroker private[Services] {
  def getObjectById[T <: StorableClass](obj: StorableObject[T], id: Int) : Option[T]
  def getObjectsByIds[T <: StorableClass](obj: StorableObject[T], ids: List[Int], fetchSize: Int = 50): List[T]
  def getObjectsByFilters[T <: StorableClass](obj: StorableObject[T], filters: List[Filter], fetchSize: Int = 50): List[T]
  def getAllObjectsOfClass[T <: StorableClass](obj: StorableObject[T]): List[T]

  def commitObjectToDatabase(i: StorableClass): Unit
}
