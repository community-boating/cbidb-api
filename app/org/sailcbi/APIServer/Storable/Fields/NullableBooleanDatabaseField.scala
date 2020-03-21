package org.sailcbi.APIServer.Storable.Fields

import org.sailcbi.APIServer.Services.PermissionsAuthority
import org.sailcbi.APIServer.Services.PermissionsAuthority.{PERSISTENCE_SYSTEM_MYSQL, PERSISTENCE_SYSTEM_ORACLE}
import org.sailcbi.APIServer.Storable.StorableQuery.{ColumnAlias, TableAlias}
import org.sailcbi.APIServer.Storable.{Filter, ProtoStorable, StorableClass, StorableObject}

class NullableBooleanDatabaseField(override val entity: StorableObject[_ <: StorableClass], persistenceFieldName: String)(implicit PA: PermissionsAuthority) extends DatabaseField[Option[Boolean]](entity, persistenceFieldName) {
	def getFieldLength: Int = 1

	def getFieldType: String = getFieldLength match {
		case l if l == 1 => "char(" + getFieldLength + ")"
		case _ => PA.persistenceSystem match {
			case PERSISTENCE_SYSTEM_MYSQL => "char(1)"
			case PERSISTENCE_SYSTEM_ORACLE => "char(1)"
		}
	}

	def findValueInProtoStorableImpl[T](row: ProtoStorable[T], key: T): Option[Option[Boolean]] = {
		row.stringFields.get(key) match {
			case Some(Some("Y")) => Some(Some(true))
			case Some(Some("N")) => Some(Some(false))
			case Some(None) => Some(None)
			case _ => None
		}
	}

	def equals(b: Option[Boolean]): String => Filter = t => b match {
		case Some(x) => Filter(s"$t.$getPersistenceFieldName = '${if (x) "Y" else "N"}'")
		case None => Filter(s"$t.$getPersistenceFieldName IS NULL")
	}

	def getValueFromString(s: String): Option[Option[Boolean]] = s.toLowerCase match {
		case "true" => Some(Some(true))
		case "false" => Some(Some(false))
		case "" => Some(None)
		case _ => None
	}

	def alias(tableAlias: TableAlias): ColumnAlias[Option[Boolean], NullableBooleanDatabaseField] = ColumnAlias(tableAlias, this)
}