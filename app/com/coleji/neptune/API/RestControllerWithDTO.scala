package com.coleji.neptune.API

import com.coleji.neptune.Core.{PermissionsAuthority, UnlockedRequestCache}
import com.coleji.neptune.Storable.{DTOClass, StorableClass, StorableObject}

abstract class RestControllerWithDTO[S <: StorableClass, D <: DTOClass[S]](obj: StorableObject[S]) {
	protected def runValidationsForUpdate(rc: UnlockedRequestCache, d: D): ValidationResult
	protected def runValidationsForInsert(rc: UnlockedRequestCache, d: D): ValidationResult

	protected def put(rc: UnlockedRequestCache, dto: D)(implicit PA: PermissionsAuthority): Either[ValidationError, S] = {
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