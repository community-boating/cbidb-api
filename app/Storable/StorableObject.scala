package Storable

import Storable.Fields.DatabaseField

trait StorableObject[T <: StorableClass] {
  type ThisClass = T
  val self: StorableObject[T] = this
  val entityName: String
  val fields: FieldsObject
  val fieldList: List[DatabaseField[_]]
  val primaryKeyName: String

  def construct(r: DatabaseRow): ThisClass

  def getSeedData: Set[T]
}