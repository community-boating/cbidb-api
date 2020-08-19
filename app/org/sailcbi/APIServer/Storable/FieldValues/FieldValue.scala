package org.sailcbi.APIServer.Storable.FieldValues

import org.sailcbi.APIServer.Storable.Fields.DatabaseField
import org.sailcbi.APIServer.Storable.StorableClass

abstract class FieldValue[T](instance: StorableClass, field: DatabaseField[T]) {
	private var value: Option[T] = None

	override def toString: String = peek match {
		case None => "(unset)"
		case Some(t: T) => t.toString
	}

	def set(v: T): Unit = {
	//	println("setting: " + instance.getCompanion.entityName + "." + field.getPersistenceFieldName)
		value = Some(v)
		//instance.setDirty
	}

	def peek: Option[T] = value

	def get: T = value match {
		case Some(v) => v
		case None => throw new Exception("Attemted to get() an unset/unretrieved FieldValue " + instance.getCompanion.entityName + "." + field.getPersistenceFieldName)
	}

	def isSet: Boolean = value match {
		case Some(_) => true
		case None => false
	}

	def getPersistenceFieldName: String = field.getPersistenceFieldName

	def getPersistenceLiteral: (String, List[String])

	def getField: DatabaseField[T] = field
}

object FieldValue {
	object OrderByPersistenceName extends Ordering[FieldValue[_]] {
		def compare(a: FieldValue[_], b: FieldValue[_]) = a.getPersistenceFieldName compare b.getPersistenceFieldName
	}
}