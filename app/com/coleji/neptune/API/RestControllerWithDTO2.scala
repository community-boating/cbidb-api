package com.coleji.neptune.API

import com.coleji.neptune.Core.{PermissionsAuthority, UnlockedRequestCache}
import com.coleji.neptune.Storable.{StorableClass, StorableObject}

import scala.reflect.runtime.universe

abstract class RestControllerWithDTO2[S <: StorableClass, D](obj: StorableObject[S])(implicit manifest: scala.reflect.Manifest[S]) {
	protected def runValidationsForUpdate(rc: UnlockedRequestCache, dto: D): ValidationResult
	protected def runValidationsForInsert(rc: UnlockedRequestCache, dto: D): ValidationResult
	protected def mutateStorableForUpdate(storable: S, dto: D): S
	protected def mutateStorableForInsert(storable: S, dto: D): S
	protected def getId(dto: D): Option[Int]

	protected def mutateDtoBeforeOperating(dto: D): D = dto

	lazy private val runtimeMirror = universe.runtimeMirror(getClass.getClassLoader)

	protected def put(rc: UnlockedRequestCache, rawDto: D)(implicit PA: PermissionsAuthority): Either[ValidationError, S] = {
		rc.withTransaction(() => {
			val dto = mutateDtoBeforeOperating(rawDto)
			val result = recurseThroughObject(rc, dto, obj)
			result match {
				case Left(e) => {
					Left(e)
				}
				case Right(s) => recurse(rc, s) match {
					case e: ValidationError => {
						Left(e)
					}
					case ValidationOk => Right(s)
				}
			}
		})
	}

	private def recurseThroughObject(rc: UnlockedRequestCache, dto: D, obj: StorableObject[S]): Either[ValidationError, S] = {
		getId(dto) match {
			case Some(dtoId: Int) => {
				runValidationsForUpdate(rc, dto) match {
					case ve: ValidationError => Left(ve)
					case ValidationOk => {
						println("about to fetch " + obj.entityName + "#" + dtoId)
						val storable = rc.getObjectById(obj, dtoId, Set(obj.primaryKey)).get
						mutateStorableForUpdate(storable, dto)
						rc.commitObjectToDatabase(storable)
						Right(storable)
					}
				}
			}
			case None => {
				runValidationsForInsert(rc, dto) match {
					case ve: ValidationError => Left(ve)
					case ValidationOk => {
						val storable = unpackage(rc, dto)
						rc.commitObjectToDatabase(storable)
						Right(storable)
					}
				}
			}
		}
	}

	private def unpackage(rc: UnlockedRequestCache, dto: D): S = {
		val s: S = manifest.runtimeClass.newInstance.asInstanceOf[S]
		mutateStorableForInsert(s, dto)
		//		getCaseValues.foreach(tup => {
		//			val (name, value) = tup
		//			s.valuesList.find(_.persistenceFieldName == name).map(fv => {
		//				fv.asInstanceOf[FieldValue[Any]].update(value)
		//			})
		//		})
		//		s
	}

	private def recurse(rc: UnlockedRequestCache, s: S): ValidationResult = ValidationOk
}
