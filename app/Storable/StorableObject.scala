package Storable

import java.time.{LocalDate, LocalDateTime}

import Storable.Fields.FieldValue._
import Storable.Fields._
import oracle.net.aso.i

import scala.Function.tupled
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
  def primaryKeyName: String = primaryKey.getPersistenceFieldName

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
        case i: IntDatabaseField =>
          i.setRuntimeFieldName(name)
          intMap += (name -> i)
        case ni: NullableIntDatabaseField =>
          ni.setRuntimeFieldName(name)
          nullableIntMap += (name -> ni)
        case s: StringDatabaseField =>
          s.setRuntimeFieldName(name)
          stringMap += (name -> s)
        case ns: NullableStringDatabaseField =>
          ns.setRuntimeFieldName(name)
          nullableStringMap += (name -> ns)
        case b: BooleanDatabaseField =>
          b.setRuntimeFieldName(name)
          booleanMap += (name -> b)
        case d: DateDatabaseField =>
          d.setRuntimeFieldName(name)
          dateMap += (name -> d)
        case dt: DateTimeDatabaseField =>
          dt.setRuntimeFieldName(name)
          dateTimeMap += (name -> dt)
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

  def construct(ps: ProtoStorable, isClean: Boolean): T = {
    val embryo: T = manifest.runtimeClass.newInstance.asInstanceOf[T]

    intFieldMap.foreach(tupled((fieldName: String, field: IntDatabaseField) => {
      embryo.intValueMap.get(fieldName) match {
        case Some(fv: IntFieldValue) => field.findValueInProtoStorable(ps) match {
          case Some(i: Int) => fv.set(i)
          case None =>
        }
        case _ => throw new Exception("Field mismatch error between class and object for entity " + entityName + " field " + fieldName)
      }
    }))

    nullableIntFieldMap.foreach(tupled((fieldName: String, field: NullableIntDatabaseField) => {
      embryo.nullableIntValueMap.get(fieldName) match {
        case Some(fv: NullableIntFieldValue) => field.findValueInProtoStorable(ps) match {
          case Some(Some(i: Int)) => fv.set(Some(i))
          case Some(None) => fv.set(None)
          case None =>
        }
        case _ => throw new Exception("Field mismatch error between class and object for entity " + entityName + " field " + fieldName)
      }
    }))

    stringFieldMap.foreach(tupled((fieldName: String, field: StringDatabaseField) => {
      embryo.stringValueMap.get(fieldName) match {
        case Some(fv: StringFieldValue) => field.findValueInProtoStorable(ps) match {
          case Some(s: String) => fv.set(s)
          case None =>
        }
        case _ => throw new Exception("Field mismatch error between class and object for entity " + entityName + " field " + fieldName)
      }
    }))

    nullableStringFieldMap.foreach(tupled((fieldName: String, field: NullableStringDatabaseField) => {
      embryo.nullableStringValueMap.get(fieldName) match {
        case Some(fv: NullableStringFieldValue) => field.findValueInProtoStorable(ps) match {
          case Some(Some(s: String)) => fv.set(Some(s))
          case Some(None) => fv.set(None)
          case None =>
        }
        case _ => throw new Exception("Field mismatch error between class and object for entity " + entityName + " field " + fieldName)
      }
    }))

    booleanFieldMap.foreach(tupled((fieldName: String, field: BooleanDatabaseField) => {
      embryo.booleanValueMap.get(fieldName) match {
        case Some(fv: BooleanFieldValue) => field.findValueInProtoStorable(ps) match {
          case Some(b: Boolean) => fv.set(b)
          case None =>
        }
        case _ => throw new Exception("Field mismatch error between class and object for entity " + entityName + " field " + fieldName)
      }
    }))

    dateFieldMap.foreach(tupled((fieldName: String, field: DateDatabaseField) => {
      embryo.dateValueMap.get(fieldName) match {
        case Some(fv: DateFieldValue) => field.findValueInProtoStorable(ps) match {
          case Some(d: LocalDate) => fv.set(d)
          case None =>
        }
        case _ => throw new Exception("Field mismatch error between class and object for entity " + entityName + " field " + fieldName)
      }
    }))

    dateTimeFieldMap.foreach(tupled((fieldName: String, field: DateTimeDatabaseField) => {
      embryo.dateTimeValueMap.get(fieldName) match {
        case Some(fv: DateTimeFieldValue) => field.findValueInProtoStorable(ps) match {
          case Some(dt: LocalDateTime) => fv.set(dt)
          case None =>
        }
        case _ => throw new Exception("Field mismatch error between class and object for entity " + entityName + " field " + fieldName)
      }
    }))

    embryo
  }

  def getSeedData: Set[T]
}