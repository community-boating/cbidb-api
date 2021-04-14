package com.coleji.framework.Storable

import com.coleji.framework.Core.PermissionsAuthority.PersistenceSystem
import com.coleji.framework.Storable.FieldValues.{FieldValue, _}
import com.coleji.framework.Storable.Fields.IntDatabaseField
import com.coleji.framework.Util.Initializable
import play.api.libs.json.{JsObject, JsValue}


abstract class StorableClass(val companion: StorableObject[_ <: StorableClass])(implicit persistenceSystem: PersistenceSystem) {
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

	// If you need a self in the entity, call it myself or something
	// Can't take the final off this and override it, or everything crashes horribly.
	final val self: StorableClass = this

	val values: ValuesObject

	object noReferences extends ReferencesObject { }
	val references: ReferencesObject = noReferences

	override def toString: String = this.valuesList.toString()

	val desiredPrimaryKey = new Initializable[Int]

	def withPK(pk: Int): StorableClass = {
		desiredPrimaryKey.set(pk)
		this
	}

	override def equals(that: Any): Boolean = that match {
		case i: StorableClass => {
			try {
				val myId = this.getID
				val theirId = i.getID
				this.companion == i.companion && myId == theirId
			} catch {
				case _: Throwable => false
			}
		}
		case _ => false
	}

	override def hashCode(): Int = {
		// If this has a PK, hash the companion and pk together
		try {
			val id = this.getID
			(this.companion, id).hashCode()
		} catch {
			case _: Throwable => super.hashCode()
		}
	}

	def getPrimaryKeyFieldValue: IntFieldValue = {
		val primaryKey: IntDatabaseField = companion.primaryKey
		val primaryKeyFieldRuntimeName: String = primaryKey.getRuntimeFieldName
		intValueMap(primaryKeyFieldRuntimeName)
	}

	def initializePrimaryKeyValue(pk: Int): Unit = {
		getPrimaryKeyFieldValue.initialize(pk)
	}

	def getID: Int = getPrimaryKeyFieldValue.get

	def hasID: Boolean = getPrimaryKeyFieldValue.peek match {
		case Some(_) => true
		case None => false
	}

	lazy final val valuesList: List[FieldValue[_]] = this.values.getClass.getDeclaredFields.map(f => {
		f.setAccessible(true)
		f.get(this.values).asInstanceOf[FieldValue[_]]
	}).toList

	lazy final val referencesList: List[(String, Initializable[Object])] = this.references.getClass.getDeclaredFields.map(f => {
		f.setAccessible(true)
		(f.getName, f.get(this.references).asInstanceOf[Initializable[Object]])
	}).toList

	def hasValuesList: Boolean = valuesList.nonEmpty

	def update[T](getFieldValue: this.values.type => FieldValue[T], value: T): this.type = {
		val fieldValue = getFieldValue(this.values)
		fieldValue.update(value)
		this
	}

	def unsetRequiredFields: List[FieldValue[_]] = {
		valuesList.filter(f => !f.getField.isNullable && f.getField.getRuntimeFieldName != companion.primaryKey.getRuntimeFieldName).filter(!_.isSet)
	}

	def isDirty: Boolean = valuesList.foldLeft(false)((agg, a) => agg || a.isDirty)

	def extraFieldsForJSValue: Map[String, JsValue] = Map.empty

	def asJsValue: JsValue = {
		var map = valuesList.filter(_.isSet).map(v => v.getPersistenceFieldName -> v.asJSValue).toMap ++ this.extraFieldsForJSValue
		def addObject(name: String, o: StorableClass): Unit = {
			map += (("$$" + name) -> o.asJsValue)
		}
		referencesList.filter(_._2.isInitialized).foreach(Function.tupled((name, ref) => {
			ref.get match {
				case Some(o: StorableClass) => addObject(name, o)
				case o: StorableClass => addObject(name, o)
				case _ =>
			}
		}))
		JsObject(map)
	}

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
