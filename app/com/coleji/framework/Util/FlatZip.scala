package com.coleji.framework.Util

object FlatZip {
	def apply[T, U, V](l: List[((T, U), V)]): List[(T, U, V)] =
		l.map(t => (t._1._1, t._1._2, t._2))
}
