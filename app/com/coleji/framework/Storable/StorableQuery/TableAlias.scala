package com.coleji.framework.Storable.StorableQuery

import com.coleji.framework.Storable.{StorableClass, StorableObject}
import com.coleji.framework.Storable.StorableObject

sealed abstract class TableAlias(val obj: StorableObject[_ <: StorableClass], val name: String) {
	// TODO: throw if name has invalid characters.  for safety i think only [a-zA-Z]
}

case class TableAliasInnerJoined(override val obj: StorableObject[_ <: StorableClass], override val name: String) extends TableAlias(obj, name)
case class TableAliasOuterJoined(override val obj: StorableObject[_ <: StorableClass], override val name: String) extends TableAlias(obj, name)

object TableAlias {
	def wrapForInnerJoin(obj: StorableObject[_ <: StorableClass]): TableAliasInnerJoined = TableAliasInnerJoined(obj, obj.entityName)
	def wrapForOuterJoin(obj: StorableObject[_ <: StorableClass]): TableAliasOuterJoined = TableAliasOuterJoined(obj, obj.entityName)
	def apply(obj: StorableObject[_ <: StorableClass]): TableAliasInnerJoined = wrapForInnerJoin(obj)
}