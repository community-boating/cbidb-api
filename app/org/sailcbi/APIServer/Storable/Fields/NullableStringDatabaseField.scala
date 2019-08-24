package org.sailcbi.APIServer.Storable.Fields

import org.sailcbi.APIServer.Services.PermissionsAuthority
import org.sailcbi.APIServer.Services.PermissionsAuthority.{PERSISTENCE_SYSTEM_MYSQL, PERSISTENCE_SYSTEM_ORACLE}
import org.sailcbi.APIServer.Storable.{Filter, ProtoStorable, StorableClass, StorableObject}

class NullableStringDatabaseField(override val entity: StorableObject[_ <: StorableClass], persistenceFieldName: String, fieldLength: Int)(implicit PA: PermissionsAuthority) extends DatabaseField[Option[String]](entity, persistenceFieldName) {
	def getFieldLength: Int = fieldLength

	def getFieldType: String = getFieldLength match {
		case l if l == 1 => "char(" + getFieldLength + ")"
		case _ => PA.getPersistenceSystem match {
			case PERSISTENCE_SYSTEM_MYSQL => "varchar(" + getFieldLength + ")"
			case PERSISTENCE_SYSTEM_ORACLE => "varchar2(" + getFieldLength + ")"
		}
	}

	def findValueInProtoStorable(row: ProtoStorable[String]): Option[Option[String]] = row.stringFields.get(this.getRuntimeFieldName)

	def equalsConstant(os: Option[String]): Filter = os match {
		case Some(s: String) => Filter(t => s"$t.$getPersistenceFieldName = '$s'")
		case None => Filter(t => s"$t.$getPersistenceFieldName IS NULL")
	}

	def equalsConstantLowercase(os: Option[String]): Filter = os match {
		case Some(s: String) => Filter(t => s"lower($t.$getPersistenceFieldName) = '${s.toLowerCase()}'")
		case None => Filter(t => s"$t.$getPersistenceFieldName IS NULL")
	}

	def getValueFromString(s: String): Option[Option[String]] = if (s == "") Some(None) else Some(Some(s))
}