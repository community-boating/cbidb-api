package org.sailcbi.APIServer.CbiUtil

class InitializableWithDefault[T](default: T) extends Initializable[T] {
	override def get: T = value match {
		case Some(_) => super.get
		case None => default
	}
}
