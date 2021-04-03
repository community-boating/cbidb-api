package org.sailcbi.APIServer.Storable

import org.sailcbi.APIServer.Services.RequestCache
import org.sailcbi.APIServer.Storable.FieldValues._
import org.sailcbi.APIServer.Storable.Fields._
import org.sailcbi.APIServer.Storable.StorableQuery.{ColumnAlias, ColumnAliasInnerJoined, QueryBuilderResultRow, TableAliasInnerJoined}

import java.time.{LocalDate, LocalDateTime}
import scala.Function.tupled
import scala.reflect.runtime.universe._

abstract class StorableObject[T <: StorableClass](implicit manifest: scala.reflect.Manifest[T]) {
	StorableObject.addEntity(this)
	type IntFieldMap = Map[String, IntDatabaseField]
	type DoubleFieldMap = Map[String, DoubleDatabaseField]
	type StringFieldMap = Map[String, StringDatabaseField]
	type DateFieldMap = Map[String, DateDatabaseField]
	type DateTimeFieldMap = Map[String, DateTimeDatabaseField]
	type BooleanFieldMap = Map[String, BooleanDatabaseField]

	type NullableIntFieldMap = Map[String, NullableIntDatabaseField]
	type NullableDoubleFieldMap = Map[String, NullableDoubleDatabaseField]
	type NullableStringFieldMap = Map[String, NullableStringDatabaseField]
	type NullableDateFieldMap = Map[String, NullableDateDatabaseField]

	val self: StorableObject[T] = this
	val entityName: String
	val fields: FieldsObject

	def primaryKey: IntDatabaseField

	def peekInstanceForID(id: Int, rc: RequestCache): Option[T] = rc.getObjectById(this, id)

	def getInstanceForID(id: Int, rc: RequestCache): T = peekInstanceForID(id, rc).get

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

		for (acc <- accessors) {
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
	lazy val stringFieldMap: StringFieldMap = fieldMaps._5
	lazy val nullableStringFieldMap: NullableStringFieldMap = fieldMaps._6
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

	// Make sure all the lazy things are actually set
	def init(): Unit = {
		fieldList
	}

	def hasValueList: Boolean = {
		val embryo: T = manifest.runtimeClass.newInstance.asInstanceOf[T]
		embryo.hasValuesList
	}

	def valueListMatchesReflection: Boolean = {
		val embryo: T = manifest.runtimeClass.newInstance.asInstanceOf[T]
		val valuesList = embryo.valuesList.sorted(FieldValue.OrderByPersistenceName)
		val valuesListReflection = embryo.getValuesListByReflection.map(_._2).sorted(FieldValue.OrderByPersistenceName)
		valuesList equals valuesListReflection
	}

	def construct(qbrr: QueryBuilderResultRow): T = construct(qbrr.ps)

	def construct(ps: ProtoStorable[ColumnAlias[_, _]]): T =
		construct(ps, None)

	def construct(ps: ProtoStorable[ColumnAlias[_, _]], tableAlias: Option[String]): T = {
		val embryo: T = manifest.runtimeClass.newInstance.asInstanceOf[T]

		type FieldDefinition = (String, DatabaseField[_])

		def aliasField[T, U <: DatabaseField[T]](f: U): ColumnAliasInnerJoined[_, _] = tableAlias match {
			case Some(a) => ColumnAliasInnerJoined[T, U](TableAliasInnerJoined(self, a), f).asInstanceOf[ColumnAliasInnerJoined[_, _]]
			case None => ColumnAlias.wrapForInnerJoin(f).asInstanceOf[ColumnAliasInnerJoined[_, _]]
		}

		val filterFunction: (FieldDefinition => Boolean) = (fd: FieldDefinition) => true

		intFieldMap.filter(t => {
			t._2 == self.primaryKey || filterFunction(t)
		}).foreach(tupled((fieldName: String, field: IntDatabaseField) => {
			embryo.intValueMap.get(fieldName) match {
				case Some(fv: IntFieldValue) => field.findValueInProtoStorable(aliasField[Int, IntDatabaseField](field), ps.asInstanceOf[ProtoStorable[ColumnAlias[_, _]]]) match {
					case Some(i: Int) => fv.initialize(i)
					case None =>
				}
				case _ => throw new Exception("Field mismatch error between class and object for entity " + entityName + " field " + fieldName)
			}
		}))

		nullableIntFieldMap.filter(filterFunction).foreach(tupled((fieldName: String, field: NullableIntDatabaseField) => {
			embryo.nullableIntValueMap.get(fieldName) match {
				case Some(fv: NullableIntFieldValue) => field.findValueInProtoStorable(aliasField[Option[Int], NullableIntDatabaseField](field), ps.asInstanceOf[ProtoStorable[ColumnAlias[_, _]]]) match {
					case Some(Some(i: Int)) => fv.initialize(Some(i))
					case Some(None) => fv.initialize(None)
					case None =>
				}
				case _ => throw new Exception("Field mismatch error between class and object for entity " + entityName + " field " + fieldName)
			}
		}))

		doubleFieldMap.filter(filterFunction).foreach(tupled((fieldName: String, field: DoubleDatabaseField) => {
			embryo.doubleValueMap.get(fieldName) match {
				case Some(fv: DoubleFieldValue) => field.findValueInProtoStorable(aliasField[Double, DoubleDatabaseField](field), ps.asInstanceOf[ProtoStorable[ColumnAlias[_, _]]]) match {
					case Some(d: Double) => fv.initialize(d)
					case None =>
				}
				case _ => throw new Exception("Field mismatch error between class and object for entity " + entityName + " field " + fieldName)
			}
		}))

		nullableDoubleFieldMap.filter(filterFunction).foreach(tupled((fieldName: String, field: NullableDoubleDatabaseField) => {
			embryo.nullableDoubleValueMap.get(fieldName) match {
				case Some(fv: NullableDoubleFieldValue) => field.findValueInProtoStorable(aliasField[Option[Double], NullableDoubleDatabaseField](field), ps.asInstanceOf[ProtoStorable[ColumnAlias[_, _]]]) match {
					case Some(Some(d: Double)) => fv.initialize(Some(d))
					case Some(None) => fv.initialize(None)
					case None =>
				}
				case _ => throw new Exception("Field mismatch error between class and object for entity " + entityName + " field " + fieldName)
			}
		}))

		stringFieldMap.filter(filterFunction).foreach(tupled((fieldName: String, field: StringDatabaseField) => {
			embryo.stringValueMap.get(fieldName) match {
				case Some(fv: StringFieldValue) => field.findValueInProtoStorable(aliasField[String, StringDatabaseField](field), ps.asInstanceOf[ProtoStorable[ColumnAlias[_, _]]]) match {
					case Some(s: String) => fv.initialize(s)
					case None =>
				}
				case _ => throw new Exception("Field mismatch error between class and object for entity " + entityName + " field " + fieldName)
			}
		}))

		nullableStringFieldMap.filter(filterFunction).foreach(tupled((fieldName: String, field: NullableStringDatabaseField) => {
			embryo.nullableStringValueMap.get(fieldName) match {
				case Some(fv: NullableStringFieldValue) => field.findValueInProtoStorable(aliasField[Option[String], NullableStringDatabaseField](field), ps.asInstanceOf[ProtoStorable[ColumnAlias[_, _]]]) match {
					case Some(Some(s: String)) => fv.initialize(Some(s))
					case Some(None) => fv.initialize(None)
					case None =>
				}
				case _ => throw new Exception("Field mismatch error between class and object for entity " + entityName + " field " + fieldName)
			}
		}))

		dateFieldMap.filter(filterFunction).foreach(tupled((fieldName: String, field: DateDatabaseField) => {
			embryo.dateValueMap.get(fieldName) match {
				case Some(fv: DateFieldValue) => field.findValueInProtoStorable(aliasField[LocalDate, DateDatabaseField](field), ps.asInstanceOf[ProtoStorable[ColumnAlias[_, _]]]) match {
					case Some(d: LocalDate) => fv.initialize(d)
					case None =>
				}
				case _ => throw new Exception("Field mismatch error between class and object for entity " + entityName + " field " + fieldName)
			}
		}))

		nullableDateFieldMap.filter(filterFunction).foreach(tupled((fieldName: String, field: NullableDateDatabaseField) => {
			embryo.nullableDateValueMap.get(fieldName) match {
				case Some(fv: NullableDateFieldValue) => field.findValueInProtoStorable(aliasField[Option[LocalDate], NullableDateDatabaseField](field), ps.asInstanceOf[ProtoStorable[ColumnAlias[_, _]]]) match {
					case Some(Some(d: LocalDate)) => fv.initialize(Some(d))
					case Some(None) => fv.initialize(None)
					case None =>
				}
				case _ => throw new Exception("Field mismatch error between class and object for entity " + entityName + " field " + fieldName)
			}
		}))

		dateTimeFieldMap.filter(filterFunction).foreach(tupled((fieldName: String, field: DateTimeDatabaseField) => {
			embryo.dateTimeValueMap.get(fieldName) match {
				case Some(fv: DateTimeFieldValue) => field.findValueInProtoStorable(aliasField[LocalDateTime, DateTimeDatabaseField](field), ps.asInstanceOf[ProtoStorable[ColumnAlias[_, _]]]) match {
					case Some(dt: LocalDateTime) => fv.initialize(dt)
					case None =>
				}
				case _ => throw new Exception("Field mismatch error between class and object for entity " + entityName + " field " + fieldName)
			}
		}))

		booleanFieldMap.filter(filterFunction).foreach(tupled((fieldName: String, field: BooleanDatabaseField) => {
			embryo.booleanValueMap.get(fieldName) match {
				case Some(fv: BooleanFieldValue) => field.findValueInProtoStorable(aliasField[Boolean, BooleanDatabaseField](field), ps.asInstanceOf[ProtoStorable[ColumnAlias[_, _]]]) match {
					case Some(b: Boolean) => fv.initialize(b)
					case None =>
				}
				case _ => throw new Exception("Field mismatch error between class and object for entity " + entityName + " field " + fieldName)
			}
		}))

		embryo
	}
}

object StorableObject {
	private var allEntitites: collection.mutable.Set[StorableObject[_]] = collection.mutable.Set.empty

	def addEntity(e: StorableObject[_]): Unit = allEntitites.add(e)

	def getEntities: List[StorableObject[_]] = allEntitites.toList
}