package com.coleji.neptune.Storable.FieldValues

import com.coleji.neptune.Core.PermissionsAuthority.PersistenceSystem
import com.coleji.neptune.Storable.Fields.DatabaseField
import com.coleji.neptune.Storable.{StorableClass, StorableObject}
import play.api.libs.json.JsValue

abstract class FieldValue[T](instance: StorableClass, @transient fieldInner: DatabaseField[T])(implicit persistenceSystem: PersistenceSystem) extends Serializable {
	private var value: Option[T] = None
	private var dirty: Boolean = false

	private val entityName: String = instance.companion.entityName
	private val fieldNameInner: String = fieldInner.persistenceFieldName

	@transient lazy val field: DatabaseField[T] = {
		if (fieldInner != null) fieldInner
		else StorableObject.getEntities.find(_.entityName == entityName).flatMap(o =>
			o.fieldList.find(_.persistenceFieldName == fieldNameInner)
		).orNull.asInstanceOf[DatabaseField[T]]
	}

	override def toString: String = persistenceFieldName + ": " + (peek match {
		case None => "(unset)"
		case Some(t: T) => t.toString
	})

	def initialize(v: T): Unit = {
		value match {
			case None => value = Some(v)
			case Some(_) => throw new Exception("Attempted to initialize over a set field value")
		}
	}

	def update(v: T): Boolean = {
		value = Some(v)
		dirty = true
		true
	}

	def unset(): Boolean = {
		value = None
		dirty = false
		true
	}

	def updateFromJsValue(v: JsValue): Boolean

	def isDirty = dirty

	def peek: Option[T] = value

	def get: T = value match {
		case Some(v) => v
		case None => throw new Exception("Attemted to get() an unset/unretrieved FieldValue " + instance.companion.entityName + "." + field.persistenceFieldName)
	}

	def isSet: Boolean = value match {
		case Some(_) => true
		case None => false
	}

	def persistenceFieldName: String = field.persistenceFieldName

	def getPersistenceLiteral: (String, List[String])

	def getField: DatabaseField[T] = field

	def asJSValue: JsValue
}

object FieldValue {
	object OrderByPersistenceName extends Ordering[FieldValue[_]] {
		def compare(a: FieldValue[_], b: FieldValue[_]) = a.persistenceFieldName compare b.persistenceFieldName
	}
}