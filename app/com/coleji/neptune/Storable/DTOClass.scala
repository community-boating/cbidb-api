package com.coleji.neptune.Storable

import com.coleji.neptune.API.{ValidationError, ValidationOk, ValidationResult}
import com.coleji.neptune.Core.UnlockedRequestCache

abstract class DTOClass[S <: StorableClass](implicit manifest: scala.reflect.Manifest[S]) {
//	protected def classAccessors: List[MethodSymbol] = typeOf[T].members.collect {
//		case m: MethodSymbol if m.isCaseAccessor => m
//	}.toList
//
//	protected def getCaseValues: List[(String, _)] = {
//		val runtimeMirror = universe.runtimeMirror(getClass.getClassLoader)
//		val instanceMirror = runtimeMirror.reflect(this)
//		classAccessors.map(ca => {
//			val fieldMirror = instanceMirror.reflectField(ca)
//			(ca.name.toString, fieldMirror.get)
//		})
//	}

	def getId: Option[Int]

	def mutateStorableForUpdate(s: S): S
	def mutateStorableForInsert(s: S): S

	def unpackage: S = {
		val s: S = manifest.runtimeClass.newInstance.asInstanceOf[S]
		mutateStorableForInsert(s)
//		getCaseValues.foreach(tup => {
//			val (name, value) = tup
//			s.valuesList.find(_.persistenceFieldName == name).map(fv => {
//				fv.asInstanceOf[FieldValue[Any]].update(value)
//			})
//		})
//		s
	}

	protected def runValidationsForUpdate(rc: UnlockedRequestCache): ValidationResult = ValidationOk
	protected def runValidationsForInsert(rc: UnlockedRequestCache): ValidationResult = ValidationOk

	def recurseThroughObject[S <: StorableClass, T <: DTOClass[S]](dto: T, obj: StorableObject[S], rc: UnlockedRequestCache): Either[ValidationError, S] = {
		dto.getId match {
			case Some(dtoId: Int) => {
				println(s"its an update: $dtoId")

				dto.runValidationsForUpdate(rc) match {
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

				dto.runValidationsForInsert(rc) match {
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

	protected def recurse(rc: UnlockedRequestCache): ValidationResult = ValidationOk
}
