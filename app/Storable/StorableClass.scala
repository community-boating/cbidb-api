package Storable

import Storable.Fields.FieldValue.FieldValue

abstract class StorableClass {
  def companion: StorableObject[_]
  def deconstruct: Set[FieldValue]
}
