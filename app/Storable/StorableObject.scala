package Storable

import Storable.Fields.{DatabaseField, IntDatabaseField, StringDatabaseField}

trait StorableObject[T <: StorableClass] {
  type ThisClass = T
  val self: StorableObject[T] = this
  val entityName: String
  val fields: FieldsObject
  val intFieldMap: Map[String, IntDatabaseField]
  val stringFieldMap: Map[String, StringDatabaseField]

  val primaryKeyName: String

  def construct(r: DatabaseRow): ThisClass = {
    val embryo = new T

    self.intFieldMap.foreach(f => {
      embryo.intValueMap.get(f._1) match {
        case Some(v) => v.set(f._2.getValue(r))
      }
    })

    self.stringFieldMap.foreach(f => {
      embryo.stringValueMap.get(f._1) match {
        case Some(v) => v.set(f._2.getValue(r))
      }
    })

    embryo
  }

  def getSeedData: Set[T]
}