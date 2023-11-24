package com.coleji.neptune.Util

class InitializableSeq[U, T <: Seq[U]] extends Initializable[T]{
	def findAllInCollection(collection: T, filter: U => Boolean): T = value match {
		case None => {
			val ret = collection.filter(filter).asInstanceOf[T]
			value = Some(ret)
			ret
		}
		case Some(t) => t
	}
}
