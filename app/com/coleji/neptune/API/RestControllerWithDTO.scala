package com.coleji.neptune.API

import com.coleji.neptune.Core.{PermissionsAuthority, UnlockedRequestCache}
import com.coleji.neptune.Storable.{DTOClass, StorableClass, StorableObject}

@deprecated
abstract class RestControllerWithDTO[S <: StorableClass, D <: DTOClass[S]](obj: StorableObject[S]) {
	protected def runValidationsForUpdate(rc: UnlockedRequestCache, d: D): ValidationResult
	protected def runValidationsForInsert(rc: UnlockedRequestCache, d: D): ValidationResult

	protected def mutateDtoBeforeOperating(dto: D): D = dto

	protected def put(rc: UnlockedRequestCache, rawDto: D)(implicit PA: PermissionsAuthority): Either[ValidationError, S] = {
		rc.withTransaction(() => {
			val dto = mutateDtoBeforeOperating(rawDto)
			val result = dto.recurseThroughObject(obj, rc)
			result match {
				case Left(e) => {
					Left(e)
				}
				case Right(s) => dto.recurse(rc, s) match {
					case e: ValidationError => {
						Left(e)
					}
					case ValidationOk => Right(s)
				}
			}
		})
	}
}
