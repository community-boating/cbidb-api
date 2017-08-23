package Storable

import Storable.Fields.FieldValue.FieldValue

abstract class StorableClass {
  def deconstruct: Set[FieldValue]
}
