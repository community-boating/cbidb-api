package org.sailcbi.APIServer.Storable.Fields

import org.sailcbi.APIServer.Services.PermissionsAuthority
import org.sailcbi.APIServer.Services.PermissionsAuthority.{PERSISTENCE_SYSTEM_MYSQL, PERSISTENCE_SYSTEM_ORACLE}
import org.sailcbi.APIServer.Storable.{Filter, ProtoStorable, StorableClass, StorableObject}

class StringDatabaseField(override val entity: StorableObject[_ <: StorableClass], persistenceFieldName: String, fieldLength: Int) extends DatabaseField[String](entity, persistenceFieldName) {
	def getFieldLength: Int = fieldLength

	def getFieldType: String = getFieldLength match {
		case l if l == 1 => "char(" + getFieldLength + ")"
		case _ => PermissionsAuthority.getPersistenceSystem match {
			case PERSISTENCE_SYSTEM_MYSQL => "varchar(" + getFieldLength + ")"
			case PERSISTENCE_SYSTEM_ORACLE => "varchar2(" + getFieldLength + ")"
		}
	}

	def findValueInProtoStorable(row: ProtoStorable[String]): Option[String] = {
		row.stringFields.get(this.getRuntimeFieldName) match {
			case Some(Some(x)) => Some(x)
			case Some(None) => throw new Exception("non-null String field " + entity.entityName + "." + this.getRuntimeFieldName + " was null in a proto")
			case _ => None
		}
	}

	def equalsConstant(c: String): Filter =
		Filter(t => s"$t.$getPersistenceFieldName = '$c'")


	def equalsConstantLowercase(c: String): Filter =
		Filter(t => s"lower($t.$getPersistenceFieldName) = '${c.toLowerCase()}'")

	def getValueFromString(s: String): Option[String] = Some(s)
}