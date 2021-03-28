package com.coleji.framework.Storable.Fields

import com.coleji.framework.Storable.{Filter, ProtoStorable, StorableClass, StorableObject}
import org.sailcbi.APIServer.Services.PermissionsAuthority
import org.sailcbi.APIServer.Services.PermissionsAuthority.{PERSISTENCE_SYSTEM_MYSQL, PERSISTENCE_SYSTEM_ORACLE}
import com.coleji.framework.Storable.StorableObject

class NullableStringDatabaseField(override val entity: StorableObject[_ <: StorableClass], persistenceFieldName: String, fieldLength: Int)(implicit PA: PermissionsAuthority) extends DatabaseField[Option[String]](entity, persistenceFieldName) {
	def getFieldLength: Int = fieldLength

	def isNullable: Boolean = true

	def getFieldType: String = getFieldLength match {
		case l if l == 1 => "char(" + getFieldLength + ")"
		case _ => PA.persistenceSystem match {
			case PERSISTENCE_SYSTEM_MYSQL => "varchar(" + getFieldLength + ")"
			case PERSISTENCE_SYSTEM_ORACLE => "varchar2(" + getFieldLength + ")"
		}
	}

	def findValueInProtoStorableImpl[T](row: ProtoStorable[T], key: T): Option[Option[String]] = row.stringFields.get(key)

	def equalsConstant(os: Option[String]): String => Filter = t => os match {
		case Some(s: String) => Filter(s"$t.$getPersistenceFieldName = ?", List(s))
		case None => Filter(s"$t.$getPersistenceFieldName IS NULL", List.empty)
	}

	def equalsConstantLowercase(os: Option[String]): String => Filter = t => os match {
		case Some(s: String) => Filter(s"lower($t.$getPersistenceFieldName) = ?", List(s.toLowerCase()))
		case None => Filter(s"$t.$getPersistenceFieldName IS NULL", List.empty)
	}

	def getValueFromString(s: String): Option[Option[String]] = if (s == "") Some(None) else Some(Some(s))

//	def alias(tableAlias: TableAliasInnerJoined): ColumnAliasInnerJoined[Option[String], NullableStringDatabaseField] = ColumnAliasInnerJoined(tableAlias, this)
//	def alias(tableAlias: TableAliasOuterJoined): ColumnAliasOuterJoined[Option[String], NullableStringDatabaseField] = ColumnAliasOuterJoined(tableAlias, this)
}