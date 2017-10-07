package Storable

import Storable.Fields.{IntDatabaseField, StringDatabaseField}

import scala.reflect.runtime.universe._

trait StorableObject[T <: StorableClass] {
  type ThisClass = T

  type IntFieldMap = Map[String, IntDatabaseField]
  type StringFieldMap = Map[String, StringDatabaseField]

  val self: StorableObject[T] = this
  val entityName: String
  val fields: FieldsObject

  val primaryKeyName: String

  private val fieldMaps = {
    val rm = scala.reflect.runtime.currentMirror
    val accessors = rm.classSymbol(fields.getClass).toType.members.collect {
      case m: MethodSymbol if m.isGetter && m.isPublic => m
    }
    val instanceMirror = rm.reflect(fields)
    val regex = "^(value|method) (.*)$".r

    var intMap: IntFieldMap = Map()
    var stringMap: StringFieldMap = Map()

    for(acc <- accessors) {
      val symbol = instanceMirror.reflectMethod(acc).symbol.toString
      val name = symbol match {
        case regex(t, name1) => name1
        case _ => throw new Exception("Unparsable reflection result: " + symbol)
      }

      instanceMirror.reflectMethod(acc).apply() match {
        case i: IntDatabaseField => intMap += (name -> i)
        case s: StringDatabaseField => stringMap += (name -> s)
        case _ => throw new Exception("Unrecognized field type")
      }
    }

    (intMap, stringMap)
  }

  val intFieldMap: IntFieldMap = fieldMaps._1
  val stringFieldMap: StringFieldMap  = fieldMaps._2

  def construct(r: DatabaseRow): ThisClass = {
    val embryo = new T

    intFieldMap.foreach(f => {
      embryo.intValueMap.get(f._1) match {
        case Some(v) => v.set(f._2.getValue(r))
      }
    })

    stringFieldMap.foreach(f => {
      embryo.stringValueMap.get(f._1) match {
        case Some(v) => v.set(f._2.getValue(r))
      }
    })

    embryo
  }

  def getSeedData: Set[T]
}