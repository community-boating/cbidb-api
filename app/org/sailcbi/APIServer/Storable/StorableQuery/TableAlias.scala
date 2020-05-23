package org.sailcbi.APIServer.Storable.StorableQuery

import org.sailcbi.APIServer.Storable.{StorableClass, StorableObject}

case class TableAlias(obj: StorableObject[_ <: StorableClass], name: String) {
	// TODO: throw if name has invalid characters.  for safety i think only [a-zA-Z]
}

object TableAlias {
	def wrap(obj: StorableObject[_ <: StorableClass]): TableAlias = TableAlias(obj, obj.entityName)
}