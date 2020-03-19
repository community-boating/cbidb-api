package org.sailcbi.APIServer.Storable.StorableQuery

import org.sailcbi.APIServer.Storable.Fields.{DatabaseField, IntDatabaseField, NullableIntDatabaseField, NullableStringDatabaseField, StringDatabaseField}
import org.sailcbi.APIServer.Storable.ProtoStorable

class QueryBuilderResultRow(ps: ProtoStorable[ColumnAlias[_]]) {
	def getValue[T](field: ColumnAlias[T]): T =
		field.field.findValueInProtoStorableAliased(field.table.name, ps.asInstanceOf[ProtoStorable[ColumnAlias[_]]]).get
}
