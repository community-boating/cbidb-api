package Storable

import Storable.Fields.FieldValue._

import scala.reflect.runtime.universe._

abstract class StorableClass {
  type IntFieldValueMap = Map[String, IntFieldValue]
  type NullableIntFieldValueMap = Map[String, NullableIntFieldValue]
  type StringFieldValueMap = Map[String, StringFieldValue]
  type NullableStringFieldValueMap = Map[String, NullableStringFieldValue]
  type BooleanFieldValueMap = Map[String, BooleanFieldValue]
  type DateFieldValueMap = Map[String, DateFieldValue]
  type DateTimeFieldValueMap = Map[String, DateTimeFieldValue]

  val values: ValuesObject

  private val valueMaps = {
    val rm = scala.reflect.runtime.currentMirror
    val accessors = rm.classSymbol(values.getClass).toType.members.collect {
      case m: MethodSymbol if m.isGetter && m.isPublic => m
    }
    val instanceMirror = rm.reflect(values)
    val regex = "^(value|method) (.*)$".r

    var intMap: IntFieldValueMap = Map()
    var nullableIntMap: NullableIntFieldValueMap = Map()
    var stringMap: StringFieldValueMap = Map()
    var nullableStringMap: NullableStringFieldValueMap = Map()
    var booleanMap: BooleanFieldValueMap = Map()
    var dateMap: DateFieldValueMap = Map()
    var dateTimeMap: DateTimeFieldValueMap = Map()

    for(acc <- accessors) {
      val symbol = instanceMirror.reflectMethod(acc).symbol.toString
      val name = symbol match {
        case regex(t, name1) => name1
        case _ => throw new Exception("Unparsable reflection result: " + symbol)
      }

      instanceMirror.reflectMethod(acc).apply() match {
        case i: IntFieldValue => intMap += (name -> i)
        case ni: NullableIntFieldValue => nullableIntMap += (name -> ni)
        case s: StringFieldValue => stringMap += (name -> s)
        case ns: NullableStringFieldValue => nullableStringMap += (name -> ns)
        case b: BooleanFieldValue => booleanMap += (name -> b)
        case d: DateFieldValue => dateMap += (name -> d)
        case dt: DateTimeFieldValue => dateTimeMap += (name -> dt)
        case _ => throw new Exception("Unrecognized field type")
      }
    }

    (intMap, nullableIntMap, stringMap, nullableStringMap, booleanMap, dateMap, dateTimeMap)
  }

  val intValueMap: IntFieldValueMap = valueMaps._1
  val nullableIntValueMap: NullableIntFieldValueMap = valueMaps._2
  val stringValueMap: StringFieldValueMap = valueMaps._3
  val nullableStringValueMap: NullableStringFieldValueMap = valueMaps._4
  val booleanValueMap: BooleanFieldValueMap = valueMaps._5
  val dateValueMap: DateFieldValueMap = valueMaps._6
  val dateTimeValueMap: DateTimeFieldValueMap = valueMaps._7
}
