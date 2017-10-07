package Storable

import Storable.Fields.FieldValue.{BooleanFieldValue, IntFieldValue, StringFieldValue}
import oracle.net.aso.s

import scala.reflect.runtime.universe._

abstract class StorableClass {
  type IntFieldValueMap = Map[String, IntFieldValue]
  type StringFieldValueMap = Map[String, StringFieldValue]
  type BooleanFieldValueMap = Map[String, BooleanFieldValue]

  val values: ValuesObject

  private val valueMaps = {
    val rm = scala.reflect.runtime.currentMirror
    val accessors = rm.classSymbol(values.getClass).toType.members.collect {
      case m: MethodSymbol if m.isGetter && m.isPublic => m
    }
    val instanceMirror = rm.reflect(values)
    val regex = "^(value|method) (.*)$".r

    var intMap: IntFieldValueMap = Map()
    var stringMap: StringFieldValueMap = Map()
    var booleanMap: BooleanFieldValueMap = Map()

    for(acc <- accessors) {
      val symbol = instanceMirror.reflectMethod(acc).symbol.toString
      val name = symbol match {
        case regex(t, name1) => name1
        case _ => throw new Exception("Unparsable reflection result: " + symbol)
      }

      instanceMirror.reflectMethod(acc).apply() match {
        case i: IntFieldValue => intMap += (name -> i)
        case s: StringFieldValue => stringMap += (name -> s)
        case b: BooleanFieldValue => booleanMap += (name -> b)
        case _ => throw new Exception("Unrecognized field type")
      }
    }

    (intMap, stringMap, booleanMap)
  }

  val intValueMap: IntFieldValueMap = valueMaps._1
  val stringValueMap: StringFieldValueMap = valueMaps._2
  val booleanValueMap: BooleanFieldValueMap = valueMaps._3
}
