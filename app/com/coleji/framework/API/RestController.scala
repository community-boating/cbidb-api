package com.coleji.framework.API

import com.coleji.framework.Storable.Fields.DatabaseField
import com.coleji.framework.Storable.{Filter, StorableClass, StorableObject}
import org.sailcbi.APIServer.UserTypes.StaffRequestCache

abstract class RestController[S <: StorableClass](obj: StorableObject[S]) {
	protected def getOne(rc: StaffRequestCache, id: Int, fieldShutter: Set[DatabaseField[_]]): Option[S] = {
		rc.getObjectById(obj, id, fieldShutter)
	}

	protected def getByFilters(rc: StaffRequestCache, filters: List[String => Filter], fieldShutter: Set[DatabaseField[_]]): List[S] = {
		rc.getObjectsByFilters(obj, filters, fieldShutter)
	}
}
