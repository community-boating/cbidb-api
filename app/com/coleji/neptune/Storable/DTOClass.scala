package com.coleji.neptune.Storable

import com.coleji.neptune.API.{ValidationError, ValidationOk, ValidationResult}
import com.coleji.neptune.Core.UnlockedRequestCache

import scala.reflect.runtime.universe
import scala.reflect.runtime.universe.{MethodSymbol, typeOf}

abstract class DTOClass[S <: StorableClass](implicit manifest: scala.reflect.Manifest[S]) {
	lazy val runtimeMirror = universe.runtimeMirror(getClass.getClassLoader)

	protected def classAccessors: List[MethodSymbol] = {
		val ret = runtimeMirror.classSymbol(this.getClass).toType.members.collect {
			case m: MethodSymbol if m.isCaseAccessor => m
		}.toList
		println("@@@@ ", ret)
		ret
	}

	protected def getCaseValues: List[(String, _)] = {
		val instanceMirror = runtimeMirror.reflect(this)
		classAccessors.map(ca => {
			val fieldMirror = instanceMirror.reflectField(ca).get
			println(fieldMirror.getClass.getCanonicalName)
			println(fieldMirror)
			if (fieldMirror.isInstanceOf[String] && fieldMirror.equals("")) {
				println("$$$$$$$$$$ FOUND AN EMPTY STRING $$$$$$$$$$")
			}
			(ca.name.toString, fieldMirror)
		})
	}

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

	def recurseThroughObject(obj: StorableObject[S], rc: UnlockedRequestCache): Either[ValidationError, S] = {
		getId match {
			case Some(dtoId: Int) => {
				println(s"its an update: $dtoId")

				// TODO: reject objects where the case type is a Strign and the value is ""

//				println(classAccessors)
//				println(getCaseValues)
				runValidationsForUpdate(rc) match {
					case ve: ValidationError => Left(ve)
					case ValidationOk => {
						println(classAccessors)
						// do update
						val storable = rc.getObjectById(obj, dtoId).get
						mutateStorableForUpdate(storable)
						rc.commitObjectToDatabase(storable)
						Right(storable)
					}
				}
			}
			case None => {
				println(s"its a create")

				runValidationsForInsert(rc) match {
					case ve: ValidationError =>Left(ve)
					case ValidationOk => {
						val storable = unpackage
						rc.commitObjectToDatabase(storable)
						Right(storable)
					}
				}
			}
		}
	}

	def recurse(rc: UnlockedRequestCache): ValidationResult = ValidationOk
}

object DTOClass {

}