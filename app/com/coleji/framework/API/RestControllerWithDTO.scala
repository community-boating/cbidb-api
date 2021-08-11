package com.coleji.framework.API

import com.coleji.framework.Core.{PermissionsAuthority, UnlockedRequestCache}
import com.coleji.framework.Storable.{DTOClass, StorableClass, StorableObject}
import org.sailcbi.APIServer.Entities.EntityDefinitions.User
import org.sailcbi.APIServer.UserTypes.StaffRequestCache

abstract class RestControllerWithDTO[S <: StorableClass, D <: DTOClass[S]](obj: StorableObject[S]) {
	def runValidationsForUpdate(rc: UnlockedRequestCache, d: D): ValidationResult
	def runValidationsForInsert(rc: UnlockedRequestCache, d: D): ValidationResult

	def put(rc: StaffRequestCache, dto: D)(implicit PA: PermissionsAuthority): Either[ValidationError, S] = {
		val u = User.getAuthedUser(rc)
		if (u.values.userName.get != "JCOLE") {
			throw new Exception("Locked to jcole only")
		}

		dto.getId match {
			case Some(dtoId: Int) => {
				println(s"its an update: $dtoId")

				runValidationsForUpdate(rc, dto) match {
					case ve: ValidationError => Left(ve)
					case ValidationOk => {
						// do update
						val storable = rc.getObjectById(obj, dtoId).get
						dto.mutateStorableForUpdate(storable)
						rc.commitObjectToDatabase(storable)
						Right(storable)
					}
				}
			}
			case None => {
				println(s"its a create")

				runValidationsForInsert(rc, dto) match {
					case ve: ValidationError =>Left(ve)
					case ValidationOk => {
						val storable = dto.unpackage
						rc.commitObjectToDatabase(storable)
						Right(storable)
					}
				}
			}
		}
	}
}
