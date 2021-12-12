package com.coleji.neptune.Storable

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
}
