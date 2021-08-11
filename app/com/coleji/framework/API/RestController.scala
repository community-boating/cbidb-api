package com.coleji.framework.API

import com.coleji.framework.Storable.Fields.DatabaseField
import com.coleji.framework.Storable.{DTOClass, StorableClass, StorableObject}
import org.sailcbi.APIServer.UserTypes.StaffRequestCache

abstract class RestController[S <: StorableClass](obj: StorableObject[S]) {
	val fieldShutter: Set[DatabaseField[_]]
	def getOne(rc: StaffRequestCache, id: Int): Option[S] = {
		rc.getObjectById(obj, id, fieldShutter)
	}
}
