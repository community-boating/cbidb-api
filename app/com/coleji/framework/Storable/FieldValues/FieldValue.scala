package com.coleji.framework.Storable.FieldValues

import com.coleji.framework.Core.PermissionsAuthority.PersistenceSystem
import com.coleji.framework.Storable.Fields.DatabaseField
import com.coleji.framework.Storable.StorableClass
import play.api.libs.json.JsValue

abstract class FieldValue[T](instance: StorableClass, field: DatabaseField[T])(implicit persistenceSystem: PersistenceSystem) {
	private var value: Option[T] = None
	private var dirty: Boolean = false

	override def toString: String = getPersistenceFieldName + ": " + (peek match {
		case None => "(unset)"
		case Some(t: T) => t.toString
	})

	def initialize(v: T): Unit = {
		value match {
			case None => value = Some(v)
			case Some(_) => throw new Exception("Attempted to initialize over a set field value")
		}
	}

	def update(v: T): Unit = {
		value = Some(v)
		dirty = true
	}

	def isDirty = dirty

	def peek: Option[T] = value

	def get: T = value match {
		case Some(v) => v
		case None => throw new Exception("Attemted to get() an unset/unretrieved FieldValue " + instance.companion.entityName + "." + field.getPersistenceFieldName)
	}

	def isSet: Boolean = value match {
		case Some(_) => true
		case None => false
	}

	def getPersistenceFieldName: String = field.getPersistenceFieldName

	def getPersistenceLiteral: (String, List[String])

	def getField: DatabaseField[T] = field

	def asJSValue: JsValue
}

object FieldValue {
	object OrderByPersistenceName extends Ordering[FieldValue[_]] {
		def compare(a: FieldValue[_], b: FieldValue[_]) = a.getPersistenceFieldName compare b.getPersistenceFieldName
	}
}