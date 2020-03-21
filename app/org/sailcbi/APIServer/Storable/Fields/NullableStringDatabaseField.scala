package org.sailcbi.APIServer.Storable.Fields

import org.sailcbi.APIServer.Services.PermissionsAuthority
import org.sailcbi.APIServer.Services.PermissionsAuthority.{PERSISTENCE_SYSTEM_MYSQL, PERSISTENCE_SYSTEM_ORACLE}
import org.sailcbi.APIServer.Storable.StorableQuery.{ColumnAlias, TableAlias}
import org.sailcbi.APIServer.Storable.{Filter, ProtoStorable, StorableClass, StorableObject}

class NullableStringDatabaseField(override val entity: StorableObject[_ <: StorableClass], persistenceFieldName: String, fieldLength: Int)(implicit PA: PermissionsAuthority) extends DatabaseField[Option[String]](entity, persistenceFieldName) {
	def getFieldLength: Int = fieldLength

	def getFieldType: String = getFieldLength match {
		case l if l == 1 => "char(" + getFieldLength + ")"
		case _ => PA.persistenceSystem match {
			case PERSISTENCE_SYSTEM_MYSQL => "varchar(" + getFieldLength + ")"
			case PERSISTENCE_SYSTEM_ORACLE => "varchar2(" + getFieldLength + ")"
		}
	}

	def findValueInProtoStorableImpl[T](row: ProtoStorable[T], key: T): Option[Option[String]] = row.stringFields.get(key)

	def equalsConstant(os: Option[String]): String => Filter = t => os match {
		case Some(s: String) => Filter(s"$t.$getPersistenceFieldName = '$s'")
		case None => Filter(s"$t.$getPersistenceFieldName IS NULL")
	}

	def equalsConstantLowercase(os: Option[String]): String => Filter = t => os match {
		case Some(s: String) => Filter(s"lower($t.$getPersistenceFieldName) = '${s.toLowerCase()}'")
		case None => Filter(s"$t.$getPersistenceFieldName IS NULL")
	}

	def getValueFromString(s: String): Option[Option[String]] = if (s == "") Some(None) else Some(Some(s))

	def alias(tableAlias: TableAlias): ColumnAlias[Option[String], NullableStringDatabaseField] = ColumnAlias(tableAlias, this)
}