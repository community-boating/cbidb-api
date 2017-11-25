package Storable

import CbiUtil.Initializable
import Storable.Fields.FieldValue._
import Storable.Fields.IntDatabaseField

import scala.reflect.runtime.universe._

abstract class StorableClass {
  type IntFieldValueMap = Map[String, IntFieldValue]
  type NullableIntFieldValueMap = Map[String, NullableIntFieldValue]
  type DoubleFieldValueMap = Map[String, DoubleFieldValue]
  type NullableDoubleFieldValueMap = Map[String, NullableDoubleFieldValue]
  type StringFieldValueMap = Map[String, StringFieldValue]
  type NullableStringFieldValueMap = Map[String, NullableStringFieldValue]
  type DateFieldValueMap = Map[String, DateFieldValue]
  type NullableDateFieldValueMap = Map[String, NullableDateFieldValue]
  type DateTimeFieldValueMap = Map[String, DateTimeFieldValue]
  type BooleanFieldValueMap = Map[String, BooleanFieldValue]

  type Companion = StorableObject[_ <: StorableClass]

  final val self: StorableClass = this
  val values: ValuesObject

  private val companion = new Initializable[Companion]
  def getCompanion: Companion = companion.get
  def setCompanion(c: Companion): Unit = companion.set(c)

  override def equals(that: Any): Boolean = that match {
    case i: StorableClass => this.getID == i.getID
    case _ => false
  }
  override def hashCode: Int = (this.getClass.toString.hashCode, this.getID).hashCode()

  private def getPrimaryKeyFieldValue: IntFieldValue = {
    val companion: Companion = getCompanion
    val primaryKey: IntDatabaseField = companion.primaryKey
    val primaryKeyFieldRuntimeName: String = primaryKey.getRuntimeFieldName
    intValueMap(primaryKeyFieldRuntimeName)
  }

  def getID: Int = getPrimaryKeyFieldValue.get

  def hasID: Boolean = getPrimaryKeyFieldValue.peek match {
    case Some(_) => true
    case None => false
  }

  // "clean" = internal state of this instance is consistent with the state of the database
  // On construction, assume dirty; the codepath used by the DB adapter will set clean afterconstructing
  private var clean: Boolean = false
  def isClean: Boolean = clean
  def setClean(): Unit =
    clean = true
  def setDirty(): Unit =
    clean = false

  private val valueMaps = {
    val rm = scala.reflect.runtime.currentMirror
    val accessors = rm.classSymbol(values.getClass).toType.members.collect {
      case m: MethodSymbol if m.isGetter && m.isPublic => m
    }
    val instanceMirror = rm.reflect(values)
    val regex = "^(value|method) (.*)$".r

    var intMap: IntFieldValueMap = Map()
    var nullableIntMap: NullableIntFieldValueMap = Map()
    var doubleMap: DoubleFieldValueMap = Map()
    var nullableDoubleMap: NullableDoubleFieldValueMap = Map()
    var stringMap: StringFieldValueMap = Map()
    var nullableStringMap: NullableStringFieldValueMap = Map()
    var dateMap: DateFieldValueMap = Map()
    var nullableDateMap: NullableDateFieldValueMap = Map()
    var dateTimeMap: DateTimeFieldValueMap = Map()
    var booleanMap: BooleanFieldValueMap = Map()

    for(acc <- accessors) {
      val symbol = instanceMirror.reflectMethod(acc).symbol.toString
      val name = symbol match {
        case regex(t, name1) => name1
        case _ => throw new Exception("Unparsable reflection result: " + symbol)
      }

      instanceMirror.reflectMethod(acc).apply() match {
        case i: IntFieldValue => intMap += (name -> i)
        case ni: NullableIntFieldValue => nullableIntMap += (name -> ni)
        case d: DoubleFieldValue => doubleMap += (name -> d)
        case nd: NullableDoubleFieldValue => nullableDoubleMap += (name -> nd)
        case s: StringFieldValue => stringMap += (name -> s)
        case ns: NullableStringFieldValue => nullableStringMap += (name -> ns)
        case d: DateFieldValue => dateMap += (name -> d)
        case nd: NullableDateFieldValue => nullableDateMap += (name -> nd)
        case dt: DateTimeFieldValue => dateTimeMap += (name -> dt)
        case b: BooleanFieldValue => booleanMap += (name -> b)
        case _ => throw new Exception("Unrecognized field type")
      }
    }

    (intMap, nullableIntMap, doubleMap, nullableDoubleMap, stringMap, nullableStringMap, dateMap, nullableDateMap, dateTimeMap, booleanMap)
  }

  val intValueMap: IntFieldValueMap = valueMaps._1
  val nullableIntValueMap: NullableIntFieldValueMap = valueMaps._2
  val doubleValueMap: DoubleFieldValueMap = valueMaps._3
  val nullableDoubleValueMap: NullableDoubleFieldValueMap = valueMaps._4
  val stringValueMap: StringFieldValueMap = valueMaps._5
  val nullableStringValueMap: NullableStringFieldValueMap = valueMaps._6
  val dateValueMap: DateFieldValueMap = valueMaps._7
  val nullableDateValueMap: NullableDateFieldValueMap = valueMaps._8
  val dateTimeValueMap: DateTimeFieldValueMap = valueMaps._9
  val booleanValueMap: BooleanFieldValueMap = valueMaps._10
}
