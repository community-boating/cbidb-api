package org.sailcbi.APIServer.Storable

import org.sailcbi.APIServer.CbiUtil.Initializable
import org.sailcbi.APIServer.Storable.FieldValues.{FieldValue, _}
import org.sailcbi.APIServer.Storable.Fields.IntDatabaseField


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

	// If you need a self in the entity, call it myself or something
	// Can't take the final off this and override it, or everything crashes horribly.
	final val self: StorableClass = this
	val values: ValuesObject

	// TODO: unit test that verifies these are all correct.  Or some reflection hotness so they must be correct
	private val companion = new Initializable[Companion]

	val desiredPrimaryKey = new Initializable[Int]

	def withPK(pk: Int): StorableClass = {
		desiredPrimaryKey.set(pk)
		this
	}

	def getCompanion: Companion = companion.get

	def setCompanion(c: Companion): Unit = {
		c.init()
		companion.set(c)
	}

	override def equals(that: Any): Boolean = that match {
		case i: StorableClass => this.getID == i.getID
		case _ => false
	}

	override def hashCode: Int = (this.getClass.toString.hashCode, this.getID).hashCode()

	def getPrimaryKeyFieldValue: IntFieldValue = {
		val companion: Companion = getCompanion
		val primaryKey: IntDatabaseField = companion.primaryKey
		val primaryKeyFieldRuntimeName: String = primaryKey.getRuntimeFieldName
		intValueMap(primaryKeyFieldRuntimeName)
	}

	def setPrimaryKeyValue(pk: Int): Unit = {
		getPrimaryKeyFieldValue.set(pk)
	}

	def getID: Int = getPrimaryKeyFieldValue.get

	def hasID: Boolean = getPrimaryKeyFieldValue.peek match {
		case Some(_) => true
		case None => false
	}

	val valuesList: List[FieldValue[_]] = List.empty

	def hasValuesList: Boolean = valuesList.nonEmpty

	def set[T](getFieldValue: this.values.type => FieldValue[T], value: T): this.type = {
		val fieldValue = getFieldValue(this.values)
		fieldValue.set(value)
		this
	}

	def unsetRequiredFields: List[FieldValue[_]] = {
		valuesList.filter(f => !f.getField.isNullable && f.getField.getRuntimeFieldName != getCompanion.primaryKey.getRuntimeFieldName).filter(!_.isSet)
	}

//	override def toString: String = {
//		val entityName = this.companion
//	}

	// "clean" = internal state of this instance is consistent with the state of the database
	// On construction, assume dirty; the codepath used by the DB adapter will set clean afterconstructing
	private var clean: Boolean = false

	def isClean: Boolean = clean

	def setClean(): Unit =
		clean = true

	def setDirty(): Unit =
		clean = false

	def getValuesListByReflection: List[(String, FieldValue[_])] = {
		import scala.reflect.runtime.universe._
		val rm = scala.reflect.runtime.currentMirror
		val accessors = rm.classSymbol(values.getClass).toType.members.collect {
			case m: MethodSymbol if m.isGetter && m.isPublic => m
		}
		val instanceMirror = rm.reflect(values)
		val regex = "^(value|method) (.*)$".r

		accessors.toList.map(acc => {
			val symbol = instanceMirror.reflectMethod(acc).symbol.toString
			val name = symbol match {
				case regex(t, name1) => name1
				case _ => throw new Exception("Unparsable reflection result: " + symbol)
			}

			(name, instanceMirror.reflectMethod(acc).apply().asInstanceOf[FieldValue[_]])
		})
	}

	lazy val (intValueMap, nullableIntValueMap, doubleValueMap, nullableDoubleValueMap, stringValueMap, nullableStringValueMap, dateValueMap, nullableDateValueMap, dateTimeValueMap, booleanValueMap) = {
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

		if (valuesList.nonEmpty) {
			valuesList.foreach({
				case i: IntFieldValue => intMap += (i.getField.getRuntimeFieldName -> i)
				case ni: NullableIntFieldValue => nullableIntMap += (ni.getField.getRuntimeFieldName -> ni)
				case d: DoubleFieldValue => doubleMap += (d.getField.getRuntimeFieldName -> d)
				case nd: NullableDoubleFieldValue => nullableDoubleMap += (nd.getField.getRuntimeFieldName -> nd)
				case s: StringFieldValue => stringMap += (s.getField.getRuntimeFieldName -> s)
				case ns: NullableStringFieldValue => nullableStringMap += (ns.getField.getRuntimeFieldName -> ns)
				case d: DateFieldValue => dateMap += (d.getField.getRuntimeFieldName -> d)
				case nd: NullableDateFieldValue => nullableDateMap += (nd.getField.getRuntimeFieldName -> nd)
				case dt: DateTimeFieldValue => dateTimeMap += (dt.getField.getRuntimeFieldName -> dt)
				case b: BooleanFieldValue => booleanMap += (b.getField.getRuntimeFieldName -> b)
				case _ => throw new Exception("Unrecognized field type")
			})
		} else {
			getValuesListByReflection.foreach({
				case (name: String, i: IntFieldValue) => intMap += (name -> i)
				case (name: String, ni: NullableIntFieldValue) => nullableIntMap += (name -> ni)
				case (name: String, d: DoubleFieldValue) => doubleMap += (name -> d)
				case (name: String, nd: NullableDoubleFieldValue) => nullableDoubleMap += (name -> nd)
				case (name: String, s: StringFieldValue) => stringMap += (name -> s)
				case (name: String, ns: NullableStringFieldValue) => nullableStringMap += (name -> ns)
				case (name: String, d: DateFieldValue) => dateMap += (name -> d)
				case (name: String, nd: NullableDateFieldValue) => nullableDateMap += (name -> nd)
				case (name: String, dt: DateTimeFieldValue) => dateTimeMap += (name -> dt)
				case (name: String, b: BooleanFieldValue) => booleanMap += (name -> b)
				case _ => throw new Exception("Unrecognized field type")
			})

		}

		(intMap, nullableIntMap, doubleMap, nullableDoubleMap, stringMap, nullableStringMap, dateMap, nullableDateMap, dateTimeMap, booleanMap)
	}
}
