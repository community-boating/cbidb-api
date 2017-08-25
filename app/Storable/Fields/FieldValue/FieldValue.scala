package Storable.Fields.FieldValue

abstract class FieldValue {
  def getFieldName: String
  def getInsertValue: String
}
