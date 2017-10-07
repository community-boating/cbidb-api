package Storable

import Storable.Fields.{BooleanDatabaseField, DatabaseField, IntDatabaseField, StringDatabaseField}

import scala.reflect.runtime.universe._

abstract class StorableObject[T <: StorableClass](implicit manifest: scala.reflect.Manifest[T]) {
  type IntFieldMap = Map[String, IntDatabaseField]
  type StringFieldMap = Map[String, StringDatabaseField]
  type BooleanFieldMap = Map[String, BooleanDatabaseField]

  val self: StorableObject[T] = this
  val entityName: String
  println("def in abstract")
  val fields: FieldsObject

  val primaryKeyName: String

  // Must be lazy so that it is not evaluated until field is set by the concrete object (or else the reflection shit NPE's)
  private lazy val fieldMaps = {
    val rm = scala.reflect.runtime.currentMirror
    println("about to use")
    val accessors = rm.classSymbol(fields.getClass).toType.members.collect {
      case m: MethodSymbol if m.isGetter && m.isPublic => m
    }
    val instanceMirror = rm.reflect(fields)
    val regex = "^(value|method) (.*)$".r

    var intMap: IntFieldMap = Map()
    var stringMap: StringFieldMap = Map()
    var booleanMap : BooleanFieldMap = Map()

    for(acc <- accessors) {
      val symbol = instanceMirror.reflectMethod(acc).symbol.toString
      val name = symbol match {
        case regex(t, name1) => name1
        case _ => throw new Exception("Unparsable reflection result: " + symbol)
      }

      instanceMirror.reflectMethod(acc).apply() match {
        case i: IntDatabaseField => intMap += (name -> i)
        case s: StringDatabaseField => stringMap += (name -> s)
        case b: BooleanDatabaseField => booleanMap += (name -> b)
        case _ => throw new Exception("Unrecognized field type")
      }
    }

    (intMap, stringMap, booleanMap)
  }

  lazy val intFieldMap: IntFieldMap = fieldMaps._1
  lazy val stringFieldMap: StringFieldMap  = fieldMaps._2
  lazy val booleanFieldMap: BooleanFieldMap = fieldMaps._3

  lazy val fieldList: List[DatabaseField[_]] =
    intFieldMap.values.toList ++
    stringFieldMap.values.toList ++
    booleanFieldMap.values.toList

  def construct(r: DatabaseRow): T = {
    val embryo: T = manifest.runtimeClass.newInstance.asInstanceOf[T]

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

    booleanFieldMap.foreach(f => {
      embryo.booleanValueMap.get(f._1) match {
        case Some(v) => v.set(f._2.getValue(r))
      }
    })

    embryo
  }

  def getSeedData: Set[T]
}