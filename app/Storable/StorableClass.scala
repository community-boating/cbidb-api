package Storable

import Storable.Fields.FieldValue.{FieldValue, IntFieldValue, StringFieldValue}

abstract class StorableClass {
  val intValueMap: Map[String, IntFieldValue]
  val stringValueMap: Map[String, StringFieldValue]

  object values extends ValuesObject

  def companion: StorableObject[_]
}
