package Storable

import java.time.{LocalDate, LocalDateTime}

import Entities._
import Services.Authentication._
import Services.{PersistenceBroker, RequestCache}
import Storable.Fields.FieldValue._
import Storable.Fields._

import scala.Function.tupled
import scala.reflect.runtime.universe._

abstract class StorableObject[T <: StorableClass](implicit manifest: scala.reflect.Manifest[T]) {
  type IntFieldMap = Map[String, IntDatabaseField]
  type DoubleFieldMap = Map[String, DoubleDatabaseField]
  type StringFieldMap = Map[String, StringDatabaseField]
  type DateFieldMap = Map[String, DateDatabaseField]
  type DateTimeFieldMap =Map[String, DateTimeDatabaseField]
  type BooleanFieldMap = Map[String, BooleanDatabaseField]

  type NullableIntFieldMap = Map[String, NullableIntDatabaseField]
  type NullableDoubleFieldMap = Map[String, NullableDoubleDatabaseField]
  type NullableStringFieldMap = Map[String, NullableStringDatabaseField]
  type NullableDateFieldMap = Map[String, NullableDateDatabaseField]

  val self: StorableObject[T] = this
  val entityName: String
  val fields: FieldsObject

  // True if the entity can be queried by public users
  final def publicVisibility: EntityVisibility = EntitySecurity.publicSecurity.get(this) match {
    case Some(x) => x
    case None => EntityVisibility.ZERO_VISIBILITY
  }
  final val memberVisibility: EntityVisibility = EntityVisibility.ZERO_VISIBILITY
  final val staffVisibility: EntityVisibility = EntityVisibility.FULL_VISIBILITY

  final def getVisiblity(userType: UserType): EntityVisibility = userType match {
    case PublicUserType => publicVisibility
    case MemberUserType => memberVisibility
    case StaffUserType | RootUserType => staffVisibility
  }

  def primaryKey: IntDatabaseField

  def peekInstanceForID(id: Int, pb: PersistenceBroker): Option[T] = pb.getObjectById(this, id)
  def getInstanceForID(id: Int, pb: PersistenceBroker): T = peekInstanceForID(id, pb).get

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
    var doubleMap: DoubleFieldMap = Map()
    var nullableDoubleMap: NullableDoubleFieldMap = Map()
    var stringMap: StringFieldMap = Map()
    var nullableStringMap: NullableStringFieldMap = Map()
    var dateMap: DateFieldMap = Map()
    var nullableDateMap: NullableDateFieldMap = Map()
    var dateTimeMap: DateTimeFieldMap = Map()
    var booleanMap: BooleanFieldMap = Map()

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
        case d: DoubleDatabaseField =>
          d.setRuntimeFieldName(name)
          doubleMap += (name -> d)
        case nd: NullableDoubleDatabaseField =>
          nd.setRuntimeFieldName(name)
          nullableDoubleMap += (name -> nd)
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
        case nd: NullableDateDatabaseField =>
          nd.setRuntimeFieldName(name)
          nullableDateMap += (name -> nd)
        case dt: DateTimeDatabaseField =>
          dt.setRuntimeFieldName(name)
          dateTimeMap += (name -> dt)
        case _ => throw new Exception("Unrecognized field type")
      }
    }

    (intMap, nullableIntMap, doubleMap, nullableDoubleMap, stringMap, nullableStringMap, dateMap, nullableDateMap, dateTimeMap, booleanMap)
  }

  lazy val intFieldMap: IntFieldMap = fieldMaps._1
  lazy val nullableIntFieldMap: NullableIntFieldMap = fieldMaps._2
  lazy val doubleFieldMap: DoubleFieldMap = fieldMaps._3
  lazy val nullableDoubleFieldMap: NullableDoubleFieldMap = fieldMaps._4
  lazy val stringFieldMap: StringFieldMap  = fieldMaps._5
  lazy val nullableStringFieldMap: NullableStringFieldMap  = fieldMaps._6
  lazy val dateFieldMap: DateFieldMap = fieldMaps._7
  lazy val nullableDateFieldMap: NullableDateFieldMap = fieldMaps._8
  lazy val dateTimeFieldMap: DateTimeFieldMap = fieldMaps._9
  lazy val booleanFieldMap: BooleanFieldMap = fieldMaps._10

  lazy val fieldList: List[DatabaseField[_]] =
    intFieldMap.values.toList ++
    nullableIntFieldMap.values.toList ++
    doubleFieldMap.values.toList ++
    nullableDoubleFieldMap.values.toList ++
    stringFieldMap.values.toList ++
    nullableStringFieldMap.values.toList ++
    dateFieldMap.values.toList ++
    nullableDateFieldMap.values.toList ++
    dateTimeFieldMap.values.toList ++
    booleanFieldMap.values.toList

  def construct(ps: ProtoStorable, rc: RequestCache, isClean: Boolean): T = {
    val embryo: T = manifest.runtimeClass.newInstance.asInstanceOf[T]

    type FieldDefinition = (String, DatabaseField[_])

    val filterFunction: (FieldDefinition => Boolean) = rc.authenticatedUserType match {
      case StaffUserType | RootUserType => (t: FieldDefinition) => true
      case MemberUserType => (t: FieldDefinition) => false
      case PublicUserType => EntitySecurity.publicSecurity(self).fieldList match {
        case None => (t: FieldDefinition) => true
        case Some(s) => (t: FieldDefinition) => s contains t._2
      }
    }

    intFieldMap.filter(t => {
      t._2 == self.primaryKey || filterFunction(t)
    }).foreach(tupled((fieldName: String, field: IntDatabaseField) => {
      embryo.intValueMap.get(fieldName) match {
        case Some(fv: IntFieldValue) => field.findValueInProtoStorable(ps) match {
          case Some(i: Int) => fv.set(i)
          case None =>
        }
        case _ => throw new Exception("Field mismatch error between class and object for entity " + entityName + " field " + fieldName)
      }
    }))

    nullableIntFieldMap.filter(filterFunction).foreach(tupled((fieldName: String, field: NullableIntDatabaseField) => {
      embryo.nullableIntValueMap.get(fieldName) match {
        case Some(fv: NullableIntFieldValue) => field.findValueInProtoStorable(ps) match {
          case Some(Some(i: Int)) => fv.set(Some(i))
          case Some(None) => fv.set(None)
          case None =>
        }
        case _ => throw new Exception("Field mismatch error between class and object for entity " + entityName + " field " + fieldName)
      }
    }))

    doubleFieldMap.filter(filterFunction).foreach(tupled((fieldName: String, field: DoubleDatabaseField) => {
      embryo.doubleValueMap.get(fieldName) match {
        case Some(fv: DoubleFieldValue) => field.findValueInProtoStorable(ps) match {
          case Some(d: Double) => fv.set(d)
          case None =>
        }
        case _ => throw new Exception("Field mismatch error between class and object for entity " + entityName + " field " + fieldName)
      }
    }))

    nullableDoubleFieldMap.filter(filterFunction).foreach(tupled((fieldName: String, field: NullableDoubleDatabaseField) => {
      embryo.nullableDoubleValueMap.get(fieldName) match {
        case Some(fv: NullableDoubleFieldValue) => field.findValueInProtoStorable(ps) match {
          case Some(Some(d: Double)) => fv.set(Some(d))
          case Some(None) => fv.set(None)
          case None =>
        }
        case _ => throw new Exception("Field mismatch error between class and object for entity " + entityName + " field " + fieldName)
      }
    }))

    stringFieldMap.filter(filterFunction).foreach(tupled((fieldName: String, field: StringDatabaseField) => {
      embryo.stringValueMap.get(fieldName) match {
        case Some(fv: StringFieldValue) => field.findValueInProtoStorable(ps) match {
          case Some(s: String) => fv.set(s)
          case None =>
        }
        case _ => throw new Exception("Field mismatch error between class and object for entity " + entityName + " field " + fieldName)
      }
    }))

    nullableStringFieldMap.filter(filterFunction).foreach(tupled((fieldName: String, field: NullableStringDatabaseField) => {
      embryo.nullableStringValueMap.get(fieldName) match {
        case Some(fv: NullableStringFieldValue) => field.findValueInProtoStorable(ps) match {
          case Some(Some(s: String)) => fv.set(Some(s))
          case Some(None) => fv.set(None)
          case None =>
        }
        case _ => throw new Exception("Field mismatch error between class and object for entity " + entityName + " field " + fieldName)
      }
    }))

    dateFieldMap.filter(filterFunction).foreach(tupled((fieldName: String, field: DateDatabaseField) => {
      embryo.dateValueMap.get(fieldName) match {
        case Some(fv: DateFieldValue) => field.findValueInProtoStorable(ps) match {
          case Some(d: LocalDate) => fv.set(d)
          case None =>
        }
        case _ => throw new Exception("Field mismatch error between class and object for entity " + entityName + " field " + fieldName)
      }
    }))

    nullableDateFieldMap.filter(filterFunction).foreach(tupled((fieldName: String, field: NullableDateDatabaseField) => {
      embryo.nullableDateValueMap.get(fieldName) match {
        case Some(fv: NullableDateFieldValue) => field.findValueInProtoStorable(ps) match {
          case Some(Some(d: LocalDate)) => fv.set(Some(d))
          case Some(None) => fv.set(None)
          case None =>
        }
        case _ => throw new Exception("Field mismatch error between class and object for entity " + entityName + " field " + fieldName)
      }
    }))

    dateTimeFieldMap.filter(filterFunction).foreach(tupled((fieldName: String, field: DateTimeDatabaseField) => {
      embryo.dateTimeValueMap.get(fieldName) match {
        case Some(fv: DateTimeFieldValue) => field.findValueInProtoStorable(ps) match {
          case Some(dt: LocalDateTime) => fv.set(dt)
          case None =>
        }
        case _ => throw new Exception("Field mismatch error between class and object for entity " + entityName + " field " + fieldName)
      }
    }))

    booleanFieldMap.filter(filterFunction).foreach(tupled((fieldName: String, field: BooleanDatabaseField) => {
      embryo.booleanValueMap.get(fieldName) match {
        case Some(fv: BooleanFieldValue) => field.findValueInProtoStorable(ps) match {
          case Some(b: Boolean) => fv.set(b)
          case None =>
        }
        case _ => throw new Exception("Field mismatch error between class and object for entity " + entityName + " field " + fieldName)
      }
    }))

    embryo
  }
}