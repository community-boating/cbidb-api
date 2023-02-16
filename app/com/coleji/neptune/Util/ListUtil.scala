package com.coleji.neptune.Util

import scala.annotation.tailrec

object ListUtil {
	/**
	 * Given List l and function f, apply f to each element until a nonempty option is generated.
	 * Return such element, or None if the whole list is unsuccessfully traversed.
	 */
	@tailrec
	def findAndReturn[A, T](l: List[A])(f: A => Option[T]): Option[T] = l match {
		case e :: ll => f(e) match {
			case Some(r) => Some(r)
			case None => findAndReturn(ll)(f)
		}
		case Nil => None
	}
}
