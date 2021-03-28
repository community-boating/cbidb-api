package com.coleji.framework.Storable.Fields

import com.coleji.framework.Core.PermissionsAuthority
import com.coleji.framework.Storable.{Filter, ProtoStorable, StorableClass, StorableObject}
import com.coleji.framework.Core.PermissionsAuthority.{PERSISTENCE_SYSTEM_MYSQL, PERSISTENCE_SYSTEM_ORACLE, PERSISTENCE_SYSTEM_RELATIONAL}
import com.coleji.framework.Storable.StorableObject

class BooleanDatabaseField(override val entity: StorableObject[_ <: StorableClass], persistenceFieldName: String, nullImpliesFalse: Boolean = false)(implicit PA: PermissionsAuthority) extends DatabaseField[Boolean](entity, persistenceFieldName) {
	def getFieldLength: Int = 1

	def isNullable: Boolean = nullImpliesFalse

	def getFieldType: String = getFieldLength match {
		case _ => PA.persistenceSystem match {
			case _: PERSISTENCE_SYSTEM_RELATIONAL => "char(1)"
		}
	}

	def findValueInProtoStorableImpl[T](row: ProtoStorable[T], key: T): Option[Boolean] = {
		row.stringFields.get(key) match {
			case Some(Some("Y")) => Some(true)
			case Some(Some("N")) => Some(false)
			case Some(None) =>
				if (nullImpliesFalse) Some(false)
				else throw new NonNullFieldWasNullException("non-null Boolean field " + entity.entityName + "." + this.getRuntimeFieldName + " was null in a proto")
			case _ => None
		}
	}

	def equals(b: Boolean): String => Filter =
		t => Filter(s"$t.$getPersistenceFieldName = '${if (b) "Y" else "N"}'", List.empty)

	def getValueFromString(s: String): Option[Boolean] = s.toLowerCase match {
		case "true" => Some(true)
		case "false" => Some(false)
		case _ => None
	}

//	def alias(tableAlias: TableAliasInnerJoined): ColumnAliasInnerJoined[Boolean, BooleanDatabaseField] = ColumnAliasInnerJoined(tableAlias, this)
//	def alias(tableAlias: TableAliasOuterJoined): ColumnAliasOuterJoined[Boolean, BooleanDatabaseField] = ColumnAliasOuterJoined(tableAlias, this)
}