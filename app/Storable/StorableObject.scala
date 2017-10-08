package Storable

import Storable.Fields._

import scala.reflect.runtime.universe._

abstract class StorableObject[T <: StorableClass](implicit manifest: scala.reflect.Manifest[T]) {
  type IntFieldMap = Map[String, IntDatabaseField]
  type StringFieldMap = Map[String, StringDatabaseField]
  type BooleanFieldMap = Map[String, BooleanDatabaseField]
  type DateFieldMap = Map[String, DateDatabaseField]
  type DateTimeFieldMap =Map[String, DateTimeDatabaseField]

  type NullableIntFieldMap = Map[String, NullableIntDatabaseField]
  type NullableStringFieldMap = Map[String, NullableStringDatabaseField]
  type NullableBooleanFieldMap = Map[String, NullableBooleanDatabaseField]

  val self: StorableObject[T] = this
  val entityName: String
  val fields: FieldsObject

  val primaryKey: IntDatabaseField
  def primaryKeyName: String = primaryKey.getFieldName

  // Must be lazy so that it is not evaluated until field is set by the concrete object (or else the reflection shit NPE's)
  private lazy val fieldMaps = {
    val rm = scala.reflect.runtime.currentMirror
    val accessors = rm.classSymbol(fields.getClass).toType.members.collect {
      case m: MethodSymbol if m.isGetter && m.isPublic => m
    }
    val instanceMirror = rm.reflect(fields)
    val regex = "^(value|method) (.*)$".r

    var intMap: IntFieldMap = Map()
    var nullableIntMap: NullableIntFieldMap = Map()
    var stringMap: StringFieldMap = Map()
    var nullableStringMap: NullableStringFieldMap = Map()
    var booleanMap: BooleanFieldMap = Map()
    var dateMap: DateFieldMap = Map()
    var dateTimeMap: DateTimeFieldMap = Map()

    for(acc <- accessors) {
      val symbol = instanceMirror.reflectMethod(acc).symbol.toString
      val name = symbol match {
        case regex(t, name1) => name1
        case _ => throw new Exception("Unparsable reflection result: " + symbol)
      }

      instanceMirror.reflectMethod(acc).apply() match {
        case i: IntDatabaseField => intMap += (name -> i)
        case ni: NullableIntDatabaseField => nullableIntMap += (name -> ni)
        case s: StringDatabaseField => stringMap += (name -> s)
        case ns: NullableStringDatabaseField => nullableStringMap += (name -> ns)
        case b: BooleanDatabaseField => booleanMap += (name -> b)
        case d: DateDatabaseField => dateMap += (name -> d)
        case dt: DateTimeDatabaseField => dateTimeMap += (name -> dt)
        case _ => throw new Exception("Unrecognized field type")
      }
    }

    (intMap, nullableIntMap, stringMap, nullableStringMap, booleanMap, dateMap, dateTimeMap)
  }

  lazy val intFieldMap: IntFieldMap = fieldMaps._1
  lazy val nullableIntFieldMap: NullableIntFieldMap = fieldMaps._2
  lazy val stringFieldMap: StringFieldMap  = fieldMaps._3
  lazy val nullableStringFieldMap: NullableStringFieldMap  = fieldMaps._4
  lazy val booleanFieldMap: BooleanFieldMap = fieldMaps._5
  lazy val dateFieldMap: DateFieldMap = fieldMaps._6
  lazy val dateTimeFieldMap: DateTimeFieldMap = fieldMaps._7

  lazy val fieldList: List[DatabaseField[_]] =
    intFieldMap.values.toList ++
    nullableIntFieldMap.values.toList ++
    stringFieldMap.values.toList ++
    nullableStringFieldMap.values.toList ++
    booleanFieldMap.values.toList ++
    dateFieldMap.values.toList ++
    dateTimeFieldMap.values.toList

  def construct(r: DatabaseRow): T = {
    val embryo: T = manifest.runtimeClass.newInstance.asInstanceOf[T]

    intFieldMap.foreach(f => {
      embryo.intValueMap.get(f._1) match {
        case Some(v) => v.set(f._2.getValue(r))
        case _ => throw new Exception("Field mismatch error between class and object for entity " + entityName + " field " + f._1)
      }
    })

    nullableIntFieldMap.foreach(f => {
      embryo.nullableIntValueMap.get(f._1) match {
        case Some(v) => v.set(f._2.getValue(r))
        case _ => throw new Exception("Field mismatch error between class and object for entity " + entityName + " field " + f._1)
      }
    })

    stringFieldMap.foreach(f => {
      embryo.stringValueMap.get(f._1) match {
        case Some(v) => v.set(f._2.getValue(r))
        case _ => throw new Exception("Field mismatch error between class and object for entity " + entityName + " field " + f._1)
      }
    })

    nullableStringFieldMap.foreach(f => {
      embryo.nullableStringValueMap.get(f._1) match {
        case Some(v) => v.set(f._2.getValue(r))
        case _ => throw new Exception("Field mismatch error between class and object for entity " + entityName + " field " + f._1)
      }
    })

    booleanFieldMap.foreach(f => {
      embryo.booleanValueMap.get(f._1) match {
        case Some(v) => v.set(f._2.getValue(r))
        case _ => throw new Exception("Field mismatch error between class and object for entity " + entityName + " field " + f._1)
      }
    })

    dateFieldMap.foreach(f => {
      embryo.dateValueMap.get(f._1) match {
        case Some(v) => v.set(f._2.getValue(r))
        case _ => throw new Exception("Field mismatch error between class and object for entity " + entityName + " field " + f._1)
      }
    })

    dateTimeFieldMap.foreach(f => {
      embryo.dateTimeValueMap.get(f._1) match {
        case Some(v) => v.set(f._2.getValue(r))
        case _ => throw new Exception("Field mismatch error between class and object for entity " + entityName + " field " + f._1)
      }
    })

    embryo
  }

  def getSeedData: Set[T]
}