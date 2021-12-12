package com.coleji.neptune.API

import com.coleji.neptune.Core.UnlockedRequestCache
import com.coleji.neptune.Storable.Fields.DatabaseField
import com.coleji.neptune.Storable.{Filter, StorableClass, StorableObject}

abstract class RestController[S <: StorableClass](obj: StorableObject[S]) {
	protected def getOne(rc: UnlockedRequestCache, id: Int, fieldShutter: Set[DatabaseField[_]]): Option[S] = {
		rc.getObjectById(obj, id, fieldShutter)
	}

	protected def getByFilters(rc: UnlockedRequestCache, filters: List[String => Filter], fieldShutter: Set[DatabaseField[_]]): List[S] = {
		rc.getObjectsByFilters(obj, filters, fieldShutter)
	}
}
