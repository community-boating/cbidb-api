package com.coleji.framework.Util

class InitializableWithDefault[T](default: T) extends Initializable[T] {
	override def get: T = value match {
		case Some(_) => super.get
		case None => default
	}
}
