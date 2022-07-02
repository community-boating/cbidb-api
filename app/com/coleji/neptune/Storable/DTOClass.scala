package com.coleji.neptune.Storable

import com.coleji.neptune.API.{ValidationError, ValidationOk, ValidationResult}
import com.coleji.neptune.Core.UnlockedRequestCache
import com.coleji.neptune.Util.Profiler

import scala.reflect.runtime.universe
import scala.reflect.runtime.universe.{MethodSymbol, typeOf}

abstract class DTOClass[S <: StorableClass](implicit manifest: scala.reflect.Manifest[S]) {
	protected val CHECK_FOR_EMPTY_STRING = true

	lazy val runtimeMirror = universe.runtimeMirror(getClass.getClassLoader)

	protected def classAccessors: List[MethodSymbol] = {
		val p = new Profiler
		val ret = runtimeMirror.classSymbol(this.getClass).toType.members.collect {
			case m: MethodSymbol if m.isCaseAccessor => m
		}.toList
		println("@@@@ ", ret)
		p.lap("did class Accessors")
		ret
	}

	protected def getCaseValues: List[(String, _)] = {
		val p = new Profiler
		val instanceMirror = runtimeMirror.reflect(this)
		val ret = classAccessors.map(ca => {
			val fieldMirror = instanceMirror.reflectField(ca).get
			println(fieldMirror.getClass.getCanonicalName)
			println(fieldMirror)
			if (fieldMirror.isInstanceOf[String] && fieldMirror.equals("")) {
				println("$$$$$$$$$$ FOUND AN EMPTY STRING $$$$$$$$$$")
			}
			(ca.name.toString, fieldMirror)
		})
		p.lap("did getCaseValues")
		ret
	}

	def getId: Option[Int]

	def mutateStorableForUpdate(s: S): S
	def mutateStorableForInsert(s: S): S

	def unpackage(rc: UnlockedRequestCache): S = {
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
				runValidationsForUpdate(rc) match {
					case ve: ValidationError => Left(ve)
					case ValidationOk => {
						println("about to fetch " + obj.entityName + "#" + dtoId)
						val storable = rc.getObjectById(obj, dtoId, Set(obj.primaryKey)).get
						mutateStorableForUpdate(storable)
						rc.commitObjectToDatabase(storable)
						Right(storable)
					}
				}
			}
			case None => {
				runValidationsForInsert(rc) match {
					case ve: ValidationError => Left(ve)
					case ValidationOk => {
						val storable = unpackage(rc)
						rc.commitObjectToDatabase(storable)
						Right(storable)
					}
				}
			}
		}
	}

	def recurse(rc: UnlockedRequestCache, s: S): ValidationResult = ValidationOk
}

object DTOClass {

}